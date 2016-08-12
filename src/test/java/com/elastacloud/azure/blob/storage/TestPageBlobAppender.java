package com.elastacloud.azure.blob.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudPageBlob;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URISyntaxException;

import static org.mockito.Mockito.*;

/**
 * Created by david on 14/04/14.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { CloudStorageAccount.class, CloudBlobClient.class, CloudBlobContainer.class, CloudPageBlob.class })
@Ignore
public class TestPageBlobAppender {


    @Test
    public void TestPageBlobAppender_TestRename() throws URISyntaxException, StorageException
    {
        PowerMockito.spy(CloudPageBlob.class);

        CloudPageBlob oldBlob = PowerMockito.mock(CloudPageBlob.class);
        CloudPageBlob newBlob = PowerMockito.mock(CloudPageBlob.class);
        CloudBlobContainer container = mock(CloudBlobContainer.class);
        CloudBlobClient client = mock(CloudBlobClient.class);
        CloudStorageAccount storageAccount = mock(CloudStorageAccount.class);

        when(storageAccount.createCloudBlobClient()).thenReturn(client);
        when(client.getContainerReference("container")).thenReturn(container);
        when(container.getPageBlobReference("testLog.log")).thenReturn(oldBlob);
        when(container.getPageBlobReference("testLog.log0")).thenReturn(newBlob);

        PowerMockito.when(newBlob.exists()).thenReturn((boolean) false);
        doNothing().when(newBlob).create(1024);
        when(newBlob.startCopy(oldBlob)).thenReturn("some copy id");

        doNothing().when(oldBlob).delete();

        try
        {
            PageBlobAppender appender = new PageBlobAppender(storageAccount);
            appender.setBlobcontainer(container);
            appender.setLogFileName("testLog.log");
            appender.setMaxSize(1024);
            appender.setBlob(oldBlob);
            appender.rename();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Assert.fail("Expected no exception, but got: " + ex.getMessage());

        }

        verify(container, times(1)).getPageBlobReference("testLog.log0");
        verify(newBlob, times(1)).exists();
        verify(newBlob, times(1)).startCopy(oldBlob);
        verify(oldBlob, times(1)).delete();
    }

    @Test
    public void TestPageBlobAppender_Test_BufferIsAligned() throws URISyntaxException, StorageException {
        PowerMockito.spy(PageBlobAppender.class);
     //   PowerMockito.doCallRealMethod().when( )

    }
}
