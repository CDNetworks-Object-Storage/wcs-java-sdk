# wcs-java-sdk

This Java SDK for CDNetworks Object Storage is built on our public API specification and supports Java 1.6 and higher.

**Note**: This SDK is designed for Java SE environments and is not suitable for Android development.

## User Guide

### Setting up the Development Environment

- **Add the dependency in your Maven project:**
    
    ```
    <dependency>
        <groupId>com.chinanetcenter.wcs.sdk</groupId>
        <artifactId>wcs-java-sdk</artifactId>
        <version>2.0.x</version>
    </dependency>
    
    ```
    
- **Using the JAR file:** [Download JARs](https://wcsd.chinanetcenter.com/sdk/cnc-java-sdk-wcs.zip)
    
    Add `wcs-java-sdk-x.x.x.jar` and the third-party JARs extracted from `wcs-java-sdk-x.x.x-dependencies.zip` to your project's build path.
    

### Configuration

To use CDNetworks Object Storage, you need a valid Access Key (AK) and Secret Key (SK) for signature authentication. You also need to configure your `uploadDomain` and `managementDomain` to perform file operations. This configuration only needs to be initialized once in your application.

- Create a CDNetworks Object Storage account.
- Log in to the CDNetworks Object Storage console. You can find your AK and SK under **Security Management** -> **Key Management**, and your domains under **Domain Query**.

After obtaining your credentials, initialize the configuration as follows:

```
import com.chinanetcenter.api.util.Config;

// 1. Initialize configuration
String ak = "your access key";
String sk = "your secrete key";
String PUT_URL = "your uploadDomain";
String GET_URL = "your downloadDomain";
String MGR_URL = "your mgrDomain";
Config.init(ak,sk,PUT_URL,GET_URL,MGR_URL);

```

### File Upload

1. `returnUrl` and `callbackUrl` cannot be specified at the same time.
2. For files larger than 20 MB, we recommend using multipart upload.
3. The provided upload domain is a standard domain. For customers sensitive to upload speed, we recommend using CDNetworks' upload acceleration service.
4. The SDK supports automatic MIME type detection (see the `uploadFileForAutoMimeType` method in the demo).

There are three upload modes for different scenarios. You can choose between simple form upload or multipart upload based on the file size.

1. **Standard Upload**: After the file is uploaded, the response is controlled by the Object Storage platform.
2. **Callback Upload**: Customize the information returned to the client after the file is uploaded. This requires setting the `callbackUrl` parameter in the upload policy. The `callbackBody` parameter is optional but recommended.
3. **Notification Upload**: Submit file processing commands (such as video transcoding, image watermarking, or image resizing) at the time of upload. This requires setting the `persistentOps` and `persistentNotifyUrl` parameters in the upload policy.

### Simple Form Upload

**Example:**

```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.DateUtil;
import com.chinanetcenter.api.util.TokenUtil;
import com.chinanetcenter.api.wsbox.FileUploadManage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadDemo {
    FileUploadManage fileUploadManage = new FileUploadManage();

    public static void main(String[] args) throws FileNotFoundException {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get your uploadDomain and MgrDomain from the console
         * under Security Management -> Domain Query. Remember to add the "http://" prefix.
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "test.JPG";
        String fileKeyWithFolder = "folder/test.JPG";
        String srcFilePath = "D:\\testfile\\1m.JPG";
        UploadDemo demo = new UploadDemo();
        // Upload a local file
        // Standard upload
        demo.uploadFile(bucketName, fileKey, srcFilePath);

        // Upload with a callback and custom return body. Specify a folder.
        //demo.uploadReturnBody(bucketName, fileKeyWithFolder, srcFilePath);

        // Upload with a specified MIME type. By default, the server determines the type from the file extension or content.
        //demo.uploadMimeType(bucketName, fileKey, srcFilePath);

        // Pre-process the file after uploading.
        //demo.uploadPersistent(bucketName, fileKey, srcFilePath);

        // Automatically detect the MIME type.
        //demo.uploadFileForAutoMimeType(bucketName, fileKey, srcFilePath);

        // Upload from an input stream.
        //FileInputStream in = new FileInputStream(new File(srcFilePath));
        //demo.uploadFile(bucketName, fileKey, in);
        //demo.uploadFileForAutoMimeType(bucketName, fileKey, in);
    }

    /**
     * Uploads a file from a local path.
     * Overwrites by default.
     */
    public void uploadFile(String bucketName,String fileKey,String srcFilePath){
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uploads a file from an InputStream. The stream will be closed by the method.
     * Overwrites by default.
     */
    public void uploadFile(String bucketName,String fileKey,InputStream in){
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,in);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * To use callbacks, custom return bodies, etc., you can specify an upload policy using PutPolicy.
     * This method demonstrates setting a returnUrl, and the process is similar for callbackUrl and callbackBody.
     */
    public void uploadReturnBody(String bucketName,String fileKey,String srcFilePath){
        String returnBody = "key=$(key)&fname=$(fname)&fsize=$(fsize)&url=$(url)&hash=$(hash)&mimeType=$(mimeType)";
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1); // Enable overwrite
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1,new Date()).getTime()));
        putPolicy.setReturnBody(returnBody);
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath,putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Upload with a specified MIME type. The server defaults to detecting from the file extension or content.
     * If you specify a mimeType, it will be used as the Content-Type header for downloads.
     */
    public void uploadMimeType(String bucketName,String fileKey,String srcFilePath){
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        try {
            String uploadToken = TokenUtil.getUploadToken(putPolicy);
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("token", uploadToken);
            paramMap.put("mimeType", "application/UQ");
            HttpClientResult result = fileUploadManage.upload(paramMap,srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transcodes the file after upload.
     * On successful upload, a persistentId is returned, which can be used to query the transcoding status.
     */
    public void uploadPersistent(String bucketName,String fileKey,String srcFilePath){
        PutPolicy putPolicy = new PutPolicy();
        String returnBody = "key=$(key)&persistentId=$(persistentId)&fsize=$(fsize)";
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setPersistentOps("imageMogr2/jpg/crop/500x500/gravity/CENTER/lowpoly/1|saveas/ZnV5enRlc3Q4Mi0wMDE6ZG9fY3J5c3RhbGxpemVfZ3Jhdml0eV9jZW50ZXJfMTQ2NTkwMDg0Mi5qcGc="); // Set the video transcoding operation
        putPolicy.setPersistentNotifyUrl("http://demo1/notifyUrl"); // Set the callback URL to be notified upon completion of transcoding
        putPolicy.setReturnBody(returnBody);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath,putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
    /**
     * Uploads a file from a local path and automatically detects the MIME type.
     * Overwrites by default.
     */
    public void uploadFileForAutoMimeType(String bucketName, String fileKey, String srcFilePath) {
        try {
            HttpClientResult result = fileUploadManage.uploadForAutoMimeType(bucketName, fileKey, srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uploads a file from an InputStream and automatically detects the MIME type. The stream will be closed by the method.
     * Overwrites by default.
     */
    public void uploadFileForAutoMimeType(String bucketName, String fileKey, InputStream in) {
        try {
            HttpClientResult result = fileUploadManage.uploadForAutoMimeType(bucketName, fileKey, in);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

```

### Multipart Upload

**Example:**

```
import com.chinanetcenter.api.entity.PutPolicy;
import com.chinanetcenter.api.entity.SliceUploadHttpResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.sliceUpload.BaseBlockUtil;
import com.chinanetcenter.api.sliceUpload.JSONObjectRet;
import com.chinanetcenter.api.util.*;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.DateUtil;
import com.chinanetcenter.api.util.EncodeUtils;
import com.chinanetcenter.api.util.WetagUtil;
import com.chinanetcenter.api.wsbox.SliceUploadResumable;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SliceUploadDemo {

    public static void main(String[] args) throws FileNotFoundException {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get your uploadDomain and MgrDomain from the console
         * under Security Management -> Domain Query. Remember to add the "http://" prefix.
         */
        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/com.toycloud.MeiYe.apk";

        String srcFilePath = "D:\\testfile\\test001\\com.toycloud.MeiYe.apk";

        /**
         * Set the chunk size to 4MB to reduce the number of upload requests.
         * If you are on an unstable network, we do not recommend changing this parameter or suggest setting it to a smaller value to avoid timeouts. The default value is 256KB.
         */
        BaseBlockUtil.CHUNK_SIZE = 4 * 1024 * 1024;

        /**
         * Set the number of concurrent block uploads to speed up the process.
         * If you are on an unstable network, we do not recommend changing this parameter or suggest setting it to a smaller value to avoid timeouts. The default value is 1.
         */
        BaseBlockUtil.THREAD_NUN = 5;
        SliceUploadDemo demo = new SliceUploadDemo();
        demo.sliceUpload(bucketName,fileKey,srcFilePath);
        demo.sliceUploadForAutoMimeType(bucketName, fileKey, srcFilePath);
        /** // Second method: Do not include the key in the scope. Instead, specify it in the header.
             // This allows using the same token to upload multiple files.
        String fileKey2 = "java-sdk/com.toycloud.MeiYe2.apktest";
        String mimeType = "application/vnd.android.package-archive";
        demo.sliceUpload(bucketName,fileKey2,srcFilePath,mimeType);
         */
    }

    public void sliceUpload(final String bucketName, final String fileKey, final String filePath) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName,fileKey,filePath);
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        sliceUploadResumable.execUpload(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet);
    }

    public void sliceUpload(final String bucketName, final String fileKey, final String filePath,String mimeType) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName,fileKey,filePath);
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        Map<String,String> headMap = new HashMap<String, String>();
        headMap.put("mimeType",mimeType);
        headMap.put("key", EncodeUtils.urlsafeEncode(fileKey));
        sliceUploadResumable.execUpload(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet,headMap);
    }
    /**
     * Performs a multipart upload and automatically detects the MIME type.
     *
     * @param bucketName
     * @param fileKey
     * @param filePath
     */
    public void sliceUploadForAutoMimeType(final String bucketName, final String fileKey, final String filePath) {
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        JSONObjectRet jsonObjectRet = getJSONObjectRet(bucketName, fileKey, filePath);
        SliceUploadResumable sliceUploadResumable = new SliceUploadResumable();
        sliceUploadResumable.execUploadForAutoMimeType(bucketName, fileKey, filePath, putPolicy, null, jsonObjectRet);
    }

    public JSONObjectRet getJSONObjectRet(final String bucketName,final String fileKey,final String filePath){
        return new JSONObjectRet() {
            /**
             * This method is called back upon successful file upload.
             * Verify that the hash of the uploaded file matches the hash of the local file.
             * A mismatch may indicate the local file has been modified.
             */
            @Override
            public void onSuccess(JsonNode obj) {
                File fileHash = new File(filePath);
                String eTagHash = WetagUtil.getEtagHash(fileHash.getParent(), fileHash.getName());// Calculate the hash based on file content
                SliceUploadHttpResult result = new SliceUploadHttpResult(obj);
                if (eTagHash.equals(result.getHash())) {
                    System.out.println("Upload successful");
                } else {
                    System.out.println("hash not equal,eTagHash:" + eTagHash + " ,hash:" + result.getHash());
                }
            }

            @Override
            public void onSuccess(byte[] body) {
                System.out.println(new String(body));
            }

            // This method is called back on upload failure.
            @Override
            public void onFailure(Exception ex) {
                if (ex instanceof WsClientException) {
                    WsClientException wsClientException = (WsClientException) ex;
                    System.out.println(wsClientException.code + ":" + wsClientException.getMessage());
                }else {
                    ex.printStackTrace();
                }
                System.out.println("Upload error, " + ex.getMessage());
            }

            // For progress display, this method is called back after each block is successfully uploaded.
            @Override
            public void onProcess(long current, long total) {
                System.out.printf("%s\r", current * 100 / total + " %");
            }

            /**
             * For resumable uploads, persist the progress information.
             * By default, the SDK saves this information to a local file. If needed, you can save it to a database yourself.
             * When resuming the upload, assign the saved value to the PutExtra parameter.
             */
            @Override
            public void onPersist(JsonNode obj) {
                BaseBlockUtil.savePutExtra(bucketName, fileKey, obj);
            }
        };
    }
}

```

### Resource Management

Manage files stored in CDNetworks Object Storage, including operations like deleting, listing resources, etc.

### Deleting a File

**Example:**

```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class DeleteDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get your uploadDomain and MgrDomain from the console
         * under Security Management -> Domain Query. Remember to add the "http://" prefix.
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile.jpg";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.delete(bucketName, fileKey);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

```

### Getting File Information

Retrieves metadata for a file, including its name, size, ETag, etc.

**Example:**

```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class StatDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get your uploadDomain and MgrDomain from the console
         * under Security Management -> Domain Query. Remember to add the "http://" prefix.
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile.jpg";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.stat(bucketName, fileKey);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

```

### Listing Resources

Lists the resources within a specified bucket.

**Example:**

```
import com.chinanetcenter.api.entity.FileListObject;
import com.chinanetcenter.api.entity.FileMessageObject;
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.StringUtil;
import com.chinanetcenter.api.wsbox.OperationManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ListDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get your uploadDomain and MgrDomain from the console
         * under Security Management -> Domain Query. Remember to add the "http://" prefix.
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        try {
            ListDemo demo = new ListDemo();
            demo.listFile(bucketName);
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    public void listFile(String bucket) throws WsClientException {
        OperationManager fileManageCommand = new OperationManager();
        int querySize = 10;
        String prex = "";
        HttpClientResult result = fileManageCommand.fileList(bucket, String.valueOf(querySize), prex, "", "");
        while (result != null && result.getStatus() == 200 && StringUtil.isNotEmpty(result.getResponse()) && !"{}".equals(result.getResponse())) {
            JsonMapper objectMapper = new JsonMapper();
            try {
                FileListObject fileListObject = objectMapper.readValue(result.getResponse(), FileListObject.class);
                for (String folder : fileListObject.getCommonPrefixes()) {
                    System.out.println("folder:" + folder);
                }
                for (FileMessageObject object : fileListObject.getItems()) {
                    System.out.print("key:" + object.getKey() + "\t");
                    System.out.print("putTime:" + object.getPutTime() + "\t");
                    System.out.print("hash:" + object.getHash() + "\t");
                    System.out.print("fsize:" + object.getFsize() + "\t");
                    System.out.print("mimeType:" + object.getMimeType() + "\t");
                    System.out.println();
                }
                if (fileListObject.getItems().size() < querySize) {
                    break;
                }
                result = fileManageCommand.fileList(bucket, String.valueOf(querySize), prex, "", fileListObject.getMarker());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

```

### Copying a Resource

Copies a specified resource to a new resource with a different name. If a resource with the target name already exists, it will not be overwritten.

**Example:**

```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class CopyDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get your uploadDomain and MgrDomain from the console
         * under Security Management -> Domain Query. Remember to add the "http://" prefix.
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile.jpg";
        String newBucketName = "your-bucket";
        String newFileKey = "java-sdk/testfile2.jpg";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.copy(bucketName, fileKey,newBucketName,newFileKey);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

```

### Moving a Resource

Moves a resource to a different bucket or renames it within the same bucket. If a resource with the target name already exists, it will not be overwritten.

**Example:**

```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class MoveDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get your uploadDomain and MgrDomain from the console
         * under Security Management -> Domain Query. Remember to add the "http://" prefix.
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/testfile2.jpg";
        String newBucketName = "your-bucket";
        String newFileKey = "java-sdk/testfile3.jpg";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.move(bucketName, fileKey, newBucketName, newFileKey);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

```

### Updating a Mirrored Resource

For buckets with mirroring configured, this feature allows you to fetch a specific resource from the origin and store it in the bucket. If a resource with the same name already exists in the bucket, it will be overwritten by the resource from the origin.

**Example:**

```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

import java.util.ArrayList;

public class PreFetchDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get your uploadDomain and MgrDomain from the console
         * under Security Management -> Domain Query. Remember to add the "http://" prefix.
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        new PreFetchDemo().prefetch(bucketName);
    }

    public void prefetch(String bucketName) {
        OperationManager fileManageCommand = new OperationManager();
        String fileName1 = "testPreFetch1.png"; // File name
        String fileName2 = "testPreFetch2.png"; // File name
        ArrayList<String> fileKeys = new ArrayList<String>();
        fileKeys.add(fileName1);
        fileKeys.add(fileName2);

        try {
            HttpClientResult result = fileManageCommand.prefetch(bucketName, fileKeys);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

```

### Audio/Video Processing

Provides audio and video processing features, including transcoding, transmuxing, and concatenation. For detailed processing parameters, please refer to the [Audio/Video Processing Ops Parameter Format](https://www.google.com/search?q=https://www.cdnetworks.com/document/API/Appendix/fopsParam%23%E9%9F%B3%E8%A7%86%E9%A2%91%E5%A4%84%E7%90%86).

**Example:**

```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.util.EncodeUtils;
import com.chinanetcenter.api.wsbox.OperationManager;

public class FopsDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get your uploadDomain and MgrDomain from the console
         * under Security Management -> Domain Query. Remember to add the "http://" prefix.
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/10m2.mp4";
        // Set transcoding operation parameters
        String fops = "avthumb/mp4/s/640x360/vb/1.25m";
        // You can use the 'saveas' parameter to specify a custom name for the transcoded file.
        // If not specified, a default name will be used, and the file will be saved in the current bucket.
        // The 'saveas' value is a Base64 encoding of 'Target_Bucket_Name:Custom_File_Key'.
        String saveAsKey = EncodeUtils.urlsafeEncode(bucketName + ":1.256m.jpg");
        fops += "|saveas/" + saveAsKey;
        String notifyURL = "http://demo1/notifyUrl";  // Notification URL. This URL will be called back upon successful transcoding.
        String force = "1";
        String separate = "1";
        FopsDemo demo = new FopsDemo();
        demo.fileTrans(bucketName,fileKey,fops,notifyURL,force,separate);

    }

    public void fileTrans(String bucketName, String fileKey, String fops, String notifyURL, String force,String separate) {
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.fops(bucketName, fileKey, fops, notifyURL,force,separate);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

```

### Fetching a Remote Resource

Fetches a resource from a specified URL and stores it in a designated bucket.

**Example:**

```
import com.chinanetcenter.api.entity.FmgrParam;
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.FmgrFileManage;

import java.util.ArrayList;
import java.util.List;

public class FmgrFetchDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";
        /**
         * You can get your uploadDomain and MgrDomain from the console
         * under Security Management -> Domain Query. Remember to add the "http://" prefix.
         */
        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        FmgrFileManage fileManageCommand = new FmgrFileManage();
        try {
            List<FmgrParam> list = new ArrayList<FmgrParam>();
            FmgrParam fmgrParam = new FmgrParam();
            fmgrParam.setBucket(bucketName);
            fmgrParam.setFetchURL("https://wcs.chinanetcenter.com/indexNew/image/pic1.jpg");
            fmgrParam.setFileKey("indexNew/image/pic1.jpg");
            fmgrParam.putExtParams("fetchTS", "0");
            list.add(fmgrParam);
            FmgrParam fmgrParam2 = new FmgrParam();
            fmgrParam2.setBucket(bucketName);
            fmgrParam2.setFetchURL("https://wcs.chinanetcenter.com/indexNew/image/pic2.m3u8");
            fmgrParam2.setFileKey("indexNew/image/pic2.m3u8");
            fmgrParam.putExtParams("fetchTS", "0");
            list.add(fmgrParam2);
            String notifyURL = "http://demo1/notifyUrl";  // Notification URL. This URL will be called back upon successful processing.
            String force = "1";
            String separate = "1";
            HttpClientResult result = fileManageCommand.fmgrFetch(list, notifyURL, force, separate);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}

```
