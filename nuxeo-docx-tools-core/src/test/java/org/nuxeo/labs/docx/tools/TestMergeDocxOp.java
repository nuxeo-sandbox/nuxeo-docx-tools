package org.nuxeo.labs.docx.tools;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.util.BlobList;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.io.File;

import static org.junit.Assert.assertNotNull;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.labs.docx.tools.nuxeo-docx-tools-core")
public class TestMergeDocxOp {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void shouldCallTheOperation() throws Exception {

        File file1 = new File(getClass().getResource("/files/fragment1.docx").getPath());
        Blob blob1 = new FileBlob(file1);

        File file2 = new File(getClass().getResource("/files/fragment2.docx").getPath());
        Blob blob2 = new FileBlob(file2);

        BlobList blobList = new BlobList();
        blobList.add(blob1);
        blobList.add(blob2);

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(blobList);
        Blob result = (Blob) automationService.run(ctx, MergeDocxOp.ID);
        assertNotNull(result);

        /*File tmpFile = new File("result.docx");
        OutputStream outputStream = new FileOutputStream(tmpFile);
        IOUtils.copy(result.getStream(),outputStream);
        outputStream.close();*/
    }

}
