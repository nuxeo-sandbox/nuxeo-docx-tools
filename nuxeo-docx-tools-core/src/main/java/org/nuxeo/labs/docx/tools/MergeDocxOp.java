package org.nuxeo.labs.docx.tools;

import org.apache.commons.io.IOUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.util.BlobList;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.runtime.api.Framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 *
 */
@Operation(id=MergeDocxOp.ID, category=Constants.CAT_DOCUMENT, label="MergeDocxOp", description="Describe here what your operation does.")
public class MergeDocxOp {

    public static final String ID = "Document.MergeDocxOp";


    @Param(name = "path", required = false)
    protected String path;

    @OperationMethod
    public Blob run(BlobList blobs) {

        if (blobs.size() == 0) {
            return null;
        } else if (blobs.size() == 1) {
            return blobs.get(0);
        }

        Blob firstBlob = blobs.get(0);
        WordprocessingMLPackage target;
        try {
            target = WordprocessingMLPackage.load(firstBlob.getStream());
            for (int i=1;i<blobs.size();i++) {
                Blob currentBlob = blobs.get(i);
                insertDocx(target.getMainDocumentPart(), IOUtils.toByteArray(currentBlob.getStream()),i);
            }
            File tmpFile = Framework.createTempFile("nx-docx4j",null);
            OutputStream outputStream = new FileOutputStream(tmpFile);
            SaveToZipFile saver = new SaveToZipFile(target);
            saver.save(outputStream);
            outputStream.close();
            return new FileBlob(tmpFile);
        } catch (Exception e) {
            throw new NuxeoException(e);
        }
    }

    void insertDocx(MainDocumentPart main, byte[] bytes,long chunkId) throws Exception {
        AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/part" + (chunkId++) + ".docx"));
        afiPart.setContentType(new ContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        afiPart.setBinaryData(bytes);
        Relationship altChunkRel = main.addTargetPart(afiPart);
        CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
        chunk.setId(altChunkRel.getId());
        main.addObject(chunk);
    }

}

