## wcs-java-sdk

Please pre-install Java, JDK 1.6 or above recommended. 
Note: wcs-java-sdk is not applicable for Android at present.
### User guide
#### Preparations
 - Add dependencies to Maven project

        <dependency>
            <groupId>com.chinanetcenter.wcs.sdk</groupId>
            <artifactId>wcs-java-sdk</artifactId>
            <version>2.0.x</version>
        </dependency>

 - Download SDK


#### Configurations
**AK/SK**, **Domain name** and **Upload name** are required to accesss object storage. You can get them as following steps:

 - Apply for CDNetworks cloud storage service.
 - Log in CDNetworks SI portal, get the AccessKey and SecretKey in Security Console - AK/SK Management
 - Log in CDNetworks SI portal, get Upload Domain (puturl) and Manage Domain (mgrurl) in Bucket Overview -> Bucket Settings
 
After getting **AK/SK**, **Domain name** and **Upload name** initialize as follow：

    import com.chinanetcenter.api.util.Config;

    String ak = "your access key";
    String sk = "your secrete key";
    String PUT_URL = "your uploadDomain";
    String GET_URL = "your downloadDomain";
    String MGR_URL = "your mgrDomain";
    Config.init(ak,sk,PUT_URL,GET_URL,MGR_URL);

#### File Upload
1. **returnUrl** and **callbackUrl** cannot be specified at the same time.
2. Multipart upload is recommended when size of upload file is larger than 20MB.
3. Object Storge provides normal domain for upload. CDNetworks CDN service is recommended if it's sensitive to upload speed.
4. Auto mimetype recognization is supported in SDK.(Please reference to demo->uploadFileForAutoMimeType method)

Three upload modes are currently supported: Normal upload, callback upload, and notification upload.
1. **Normal upload:** All the upload return results are controlled by Object Storage platform.
2. **Callback upload:** Customized information is returned to client after a file is uploaded. Parameter **callbackUrl**  in upload policy is required in this way.
3. **Notification upload:** Upload a file with file processing instructions(video transcoding, image watermark, and image scaling, etc.) **persistentOps** and **persistentNotifyUrl** in upload policy is required in this way.

##### Normal Upload
**example**
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

        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "test.JPG";
        String fileKeyWithFolder = "folder/test.JPG";
        String srcFilePath = "D:\\testfile\\1m.JPG";
        UploadDemo demo = new UploadDemo();

	// normal upload
        demo.uploadFile(bucketName, fileKey, srcFilePath);
	
	// return information after upload
        //demo.uploadReturnBody(bucketName, fileKeyWithFolder, srcFilePath);
	
	// upload file with specified mimetype
        //demo.uploadMimeType(bucketName, fileKey, srcFilePath);
	
	// preprocessing after upload
        //demo.uploadPersistent(bucketName, fileKey, srcFilePath);
	
	// mimetype of file will be auto recognized when uploading 
	//demo.uploadFileForAutoMimeType(bucketName, fileKey, srcFilePath);
	
        //FileInputStream in = new FileInputStream(new File(srcFilePath));
        //demo.uploadFile(bucketName, fileKey, in);
        //demo.uploadFileForAutoMimeType(bucketName, fileKey, in);
    }

    /**
     * upload a file by path of local file
     * overwrite by default
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
     * upload by filestream
     * overwirte by default
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
     * return information after upload, specify upload policy by 'PutPolicy'
     * callbackurl, callbackbody, returnurl is just similar
     */
    public void uploadReturnBody(String bucketName,String fileKey,String srcFilePath){
        String returnBody = "key=$(key)&fname=$(fname)&fsize=$(fsize)&url=$(url)&hash=$(hash)&mimeType=$(mimeType)";
        PutPolicy putPolicy = new PutPolicy();
        putPolicy.setOverwrite(1); // file with same name in OS will be overwrite this way
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
     * upload file of specified mimetype
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
	    paramMap.put("deadline", 365);
            HttpClientResult result = fileUploadManage.upload(paramMap,srcFilePath);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * transcoding after upload
     * 'persistentId' is returned after upload, by which you can query the status of transcoding
     */
    public void uploadPersistent(String bucketName,String fileKey,String srcFilePath){
        PutPolicy putPolicy = new PutPolicy();
        String returnBody = "key=$(key)&persistentId=$(persistentId)&fsize=$(fsize)";
        putPolicy.setOverwrite(1);
        putPolicy.setDeadline(String.valueOf(DateUtil.nextDate(1, new Date()).getTime()));
        putPolicy.setScope(bucketName + ":" + fileKey);
        putPolicy.setPersistentOps("imageMogr2/jpg/crop/500x500/gravity/CENTER/lowpoly/1|saveas/ZnV5enRlc3Q4Mi0wMDE6ZG9fY3J5c3RhbGxpemVfZ3Jhdml0eV9jZW50ZXJfMTQ2NTkwMDg0Mi5qcGc="); // set video transcoding
        putPolicy.setPersistentNotifyUrl("http://demo1/notifyUrl"); // set callback url for file transcoded
        putPolicy.setReturnBody(returnBody);
        try {
            HttpClientResult result = fileUploadManage.upload(bucketName,fileKey,srcFilePath,putPolicy);
            System.out.println(result.getStatus() + ":" + result.getResponse());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
	/**
	 * upload a file by source file path, mimetype of the file will be recognized automaticlly
	 * overwrite by default
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
	 * upload files by filestream
	 * overwrite by default
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

##### Multipart Upload
**example**
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

        Config.PUT_URL = "your uploadDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/com.toycloud.MeiYe.apk";

        String srcFilePath = "D:\\testfile\\test001\\com.toycloud.MeiYe.apk";
	
        BaseBlockUtil.CHUNK_SIZE = 4 * 1024 * 1024;
	
	BaseBlockUtil.THREAD_NUN = 5；
        SliceUploadDemo demo = new SliceUploadDemo();
        demo.sliceUpload(bucketName,fileKey,srcFilePath);
		demo.sliceUploadForAutoMimeType(bucketName, fileKey, srcFilePath);
        /**  another method, 'key' is specified by 'head'. The method is used for multipart upload with one token
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
	 * multipart upload, mimetype of file is recognized automaticlly
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
             *  callback function, which is called after the file is uploaded successfully
             * check consistency of OS file and source file by hash.
             */
            @Override
            public void onSuccess(JsonNode obj) {
                File fileHash = new File(filePath);
                String eTagHash = WetagUtil.getEtagHash(fileHash.getParent(), fileHash.getName());
                SliceUploadHttpResult result = new SliceUploadHttpResult(obj);
                if (eTagHash.equals(result.getHash())) {
                    System.out.println("upload successfully");
                } else {
                    System.out.println("hash not equal,eTagHash:" + eTagHash + " ,hash:" + result.getHash());
                }
            }

            @Override
            public void onSuccess(byte[] body) {
                System.out.println(new String(body));
            }

            // callback function, which is called when the upload is failed
            @Override
            public void onFailure(Exception ex) {
                if (ex instanceof WsClientException) {
                    WsClientException wsClientException = (WsClientException) ex;
                    System.out.println(wsClientException.code + ":" + wsClientException.getMessage());
                }else {
                    ex.printStackTrace();
                }
                System.out.println("upload error, " + ex.getMessage());
            }

            // callback function, which is called to show upload progress
            @Override
            public void onProcess(long current, long total) {
                System.out.printf("%s\r", current * 100 / total + " %");
            }

            /**
             * progress information will be saved when using persistent connection to upload 
             * information is saved disk by default, you can save it in db by yourself if you need. 
             * saved information will be used as parameters for savePutExtra when resuming the transmission
             */
            @Override
            public void onPersist(JsonNode obj) {
                BaseBlockUtil.savePutExtra(bucketName, fileKey, obj);
            }
        };
    }
}
```

#### Resource Management
Manage objects in Object Storage, including delete, move, update, etc.

##### Delete Files

**example**
```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class DeleteDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";

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

##### Get File information
Get information of a file in Object Storage, including name, size, ETag, etc.

**example**
```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class StatDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";

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

##### List Resources
List files in a specified bucket.

**范例：**
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
##### Copy Resources
Copy a specified file in a bucket and then rename it. 

**example**
```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class CopyDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";

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

##### Move Resources
Move a specified file from source bucket to a target bucket. If there is a same-name file in target bucket, the movement will be failed.

**example**
```
import com.chinanetcenter.api.entity.HttpClientResult;
import com.chinanetcenter.api.exception.WsClientException;
import com.chinanetcenter.api.util.Config;
import com.chinanetcenter.api.wsbox.OperationManager;

public class MoveDemo {
    public static void main(String[] args) {
        Config.AK = "your-ak";
        Config.SK = "your-sk";

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

##### Update Mirror Bucket Resources
Update a specified file from source bucket to its mirror bucket.(Mirror bucket should be pre-created in Object Storage.)

**范例：**
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

        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        new PreFetchDemo().prefetch(bucketName);
    }

    public void prefetch(String bucketName) {
        OperationManager fileManageCommand = new OperationManager();
        String fileName1 = "testPreFetch1.png";
        String fileName2 = "testPreFetch2.png";
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
#### 音视频操作Audio/Video Proccessing

Object Storage provides audio and video proccessing, including transcoding, transmuxing, video splicing, audio splicing, etc.

**example**
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

        Config.MGR_URL = "your MgrDomain";
        String bucketName = "your-bucket";
        String fileKey = "java-sdk/10m2.mp4";
        // set transcoding parameters
        String fops = "avthumb/mp4/s/640x360/vb/1.25m";

        String saveAsKey = EncodeUtils.urlsafeEncode(bucketName + ":1.256m.jpg");
        fops += "|saveas/" + saveAsKey;
        String notifyURL = "http://demo1/notifyUrl";  
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
#### Fetch Resources
Get a file from specified URL and save it to specified bucket.

**example**
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
            String notifyURL = "http://demo1/notifyUrl";  
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

#### Download Resources
Download a file from a URL of specified domain name and file name.
```
/**
 * Download
 */
public class DownloadDemo {
    public static void main(String[] args) {
        String downloadDomain = "your download domain";
        String fileKey = "file name";
        String filePath = "local path";
        OperationManager fileManageCommand = new OperationManager();
        try {
            HttpClientResult result = fileManageCommand.download(downloadDomain, fileKey, filePath, null);
            System.out.println(result.getStatus());
        } catch (WsClientException e) {
            e.printStackTrace();
        }
    }
}
```
