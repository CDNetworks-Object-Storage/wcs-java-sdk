package com.chinanetcenter.api.entity;

/**
 * Upload policy object<br>
 * <!--
 * <table border="1px solid">
 * 	<tr>
 * 		<th>Field Name</th>
 * 		<th width="60px">Required</th>
 * 		<th>Description</th>
 * 	</tr>
 * 	<tr>
 * 		<td>scope</td>
 * 		<td>Yes</td>
 * 		<td>Specifies the target resource space (Bucket) and resource name (Key) for upload. There are two formats: 1. <bucket>, which means allowing users to upload files to the specified bucket. 2. <bucket>:<filename>, which means allowing users to upload files with the specified filename.</td>
 * 	</tr>
 * 	<tr>
 * 		<td>deadline</td>
 * 		<td>Yes</td>
 * 		<td>The deadline for upload request authorization; UNIX timestamp, unit: milliseconds. Example: 1398916800000, representing 2014-05-01 12:00:00.</td>
 * 	</tr>
 * 	<tr>
 * 		<td>returnUrl</td>
 * 		<td>No</td>
 * 		<td>After a file upload succeeds or fails on the web, the browser will perform a 303 redirect to this URL; typically used for HTML Form uploads. (1) After a successful file upload, it will redirect to <returnUrl>?upload_ret=<queryString>, where <queryString> contains the returnBody content. (2) After a failed file upload, it will redirect to <returnUrl>?code=<code>&message=<message>, where <code> is the error code and <message> is the specific error message. If returnUrl is not set, the content of returnBody will be returned directly to the client.</td>
 * 	</tr>
 * 	<tr>
 * 		<td>returnBody</td>
 * 		<td>No</td>
 * 		<td>
 * 			After a successful upload, customize the data finally returned to the uploader (this field is used with returnUrl). If you only need to return the file size and file address, just set returnBody to fname=$(fname)&fsize=$(fsize)&url=$(url).
 * 			<ul>
 * 				<li>
 * 					Custom replacement variables, format as follows: $(x:variable), example: position=$(x:position)&message=$(x:message)
 * 				</li>
 * 				<li>
 * 					Special replacement variables
 * 					<table border="1px solid">
 * 						<tr>
 * 							<th>Parameter Value</th>
 * 							<th>Description</th>
 * 						</tr>
 * 						<tr>
 * 							<td>$(bucket)</td>
 * 							<td>Get the target bucket name for upload</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(key)</td>
 * 							<td>Get the resource name of the file saved in the space</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(fname)</td>
 * 							<td>Original uploaded file name</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(hash)</td>
 * 							<td>Unique resource identifier (hash(bucket:fname))</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(fsize)</td>
 * 							<td>Resource size, in bytes</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(url)</td>
 * 							<td>The actual path to access this resource, URL-safe Base64 encoded, needs to be parsed accordingly when used</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(costTime)</td>
 * 							<td>Time consumed by this request</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(ip)</td>
 * 							<td>Source IP of this request</td>
 * 						</tr>
 * 					</table>
 * 				</li>
 * 			</ul>
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>fsizeLimit</td>
 * 		<td>No</td>
 * 		<td>Limits the size of the uploaded file, unit: bytes (Byte); uploaded content exceeding the limit will be judged as upload failure and return status code 413.</td>
 * 	</tr>
 * 	<tr>
 * 		<td>overwrite</td>
 * 		<td>No</td>
 * 		<td>Specifies whether to overwrite existing files on the server; 0: do not overwrite; 1: overwrite</td>
 * 	</tr>
 * 	<tr>
 * 		<td>callbackUrl</td>
 * 		<td>No</td>
 * 		<td>After a successful upload, Wangsu Cloud will request this callbackUrl via POST (must be a public URL address that can normally respond to HTTP/1.1 200 OK). The callbackUrl's Response is required to return data in JSON text format, i.e., Content-Type is "application/json".</td>
 * 	</tr>
 * 	<tr>
 * 		<td>callbackBody</td>
 * 		<td>否</td>
 * 		<td>
 * 			After a successful upload, the data that Wangsu Cloud submits via POST request. callbackBody is required to be a legal url query string. Such as: key=$(key) &fsize=$(fsize)
 *			<ul>
 * 				<li>
 * 					Custom replacement variables, format is as follows: $(x:variable), example: position=$(x:position)&message=$(x:message)
 * 				</li>
 * 				<li>
 * 					Special replacement variables
 * 					<table border="1px solid">
 * 						<tr>
 * 							<th>Parameter Value</th>
 * 							<th>Description</th>
 * 						</tr>
 * 						<tr>
 * 							<td>$(bucket)</td>
 * 							<td>Get the target bucket name for upload</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(key)</td>
 * 							<td>Get the resource name of the file saved in the space</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(fname)</td>
 * 							<td>Original uploaded file name</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(hash)</td>
 * 							<td>Unique resource identifier (hash(bucket:fname))</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(fsize)</td>
 * 							<td>Resource size, in bytes</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(url)</td>
 * 							<td>The actual path to access this resource, URL-safe Base64 encoded, needs to be parsed accordingly when used</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(costTime)</td>
 * 							<td>Time consumed by this request</td>
 * 						</tr>
 * 						<tr>
 * 							<td>$(ip)</td>
 * 							<td>Source IP of this request</td>
 * 						</tr>
 * 					</table>
 * 				</li>
 * 			</ul>
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>persistentOps</td>
 * 		<td>否</td>
 * 		<td>
 * 			List of processing commands to be executed after a successful upload. Each command is a standard string, and multiple commands are separated by ";"
 * 			<table border="1px solid">
 * 				<tr>
 * 					<th>Command</th>
 * 					<th>Description</th>
 * 				</tr>
 * 				<tr>
 * 					<td>avthumb/Format</td> 
 * 					<td>Format (required) - target video format (supports flv), example: avthumb/flv</td>
 * 				</tr>
 * 				<tr>
 * 					<td>vframe/<Format></td> 
 * 					<td>Format (required) - target image format, jpg, etc. Example: vframe/jpg<</td>
 * 				</tr>
 * 				<tr>
 * 					<td>vframe/offset/<Second></td> 
 * 					<td>offset/<Second> (required) - specify the time of the video frame, unit s, example: vframe/offset/7</td>
 * 				</tr>
 * 				<tr>
 * 					<td>vframe/w/<Width></td> 
 * 					<td>w/<Width> (optional) - specify the width of the captured image, unit px (if not specified, the video's default width will be used)</td>
 * 				</tr>
 * 				<tr>
 * 					<td>vframe/h/<Height></td> 
 * 					<td>h/<Height> (optional) - specify the height of the captured image, unit px (if not specified, the video's default height will be used)</td>
 * 				</tr>
 * 			</table>
 *		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>persistentNotifyUrl</td>
 * 		<td>No</td>
 * 		<td>URL for receiving pre-processing result notifications (must be a public URL address that can normally respond to HTTP/1.1 200 OK). Hint: When setting the persistentOps field, complete the persistentNotifyUrl field setting. The platform will notify you of the instruction processing result by calling the URL set in the persistentNotifyUrl field.</td>
 * 	</tr>
 * </table>
 * -->
 * @author zouhao
 * @version 1.0
 * @since 2014/02/14
 */
public class PutPolicy {

    /**
     * Specifies the target resource space (bucketName) and resource name (fileName) for upload.
     * Format is bucketName:fileName
     */
    private String scope;
    /**
     * Valid time, Long type, unit in milliseconds
     */
    private String deadline;
    /**
     * After a successful upload, customize the data finally returned to the uploader (this field is used with returnUrl).<br />
     * Returned content<br />
     * Format example: $(bucket)&$(fsize)&$(hash)&$(key)<br />
     */
    private String returnBody;
    /**
     * Specifies whether to overwrite existing files on the server.<br />
     * 1 - allow overwrite, 0 - not allow
     */
    private int overwrite;
    /**
     * Limits the size of the uploaded file
     */
    private long fsizeLimit;
    /**
     * After a successful web file upload, the URL to which the browser performs a 303 redirect
     */
    private String returnUrl;
    /**
     * Callback URL
     */
    private String callbackUrl;
    /**
     * Callback content<br />
     * Format example: <keyName>=(keyValue)&<keyName>=(keyValue)<br />
     * Must be in key-value format
     */
    private String callbackBody;
    /**
     * Persistent operation instruction list<br />
     * Convert to flv instruction: avthumb/flv/vb/1.25m<br />
     * Video screenshot instruction: vframe/jpg/offset/1<br />
     * Separated by semicolon ";"
     */
    private String persistentOps;
    /**
     * Persistent operation notification URL
     */
    private String persistentNotifyUrl;

    private String lastModifiedTime;
    private Integer instant;
    private String saveKey;
    private Long separate;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getReturnBody() {
        return returnBody;
    }

    public void setReturnBody(String returnBody) {
        this.returnBody = returnBody;
    }

    public int getOverwrite() {
        return overwrite;
    }

    public void setOverwrite(int overwrite) {
        this.overwrite = overwrite;
    }

    public long getFsizeLimit() {
        return fsizeLimit;
    }

    public void setFsizeLimit(long fsizeLimit) {
        this.fsizeLimit = fsizeLimit;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackBody() {
        return callbackBody;
    }

    public void setCallbackBody(String callbackBody) {
        this.callbackBody = callbackBody;
    }

    public String getPersistentOps() {
        return persistentOps;
    }

    public void setPersistentOps(String persistentOps) {
        this.persistentOps = persistentOps;
    }

    public String getPersistentNotifyUrl() {
        return persistentNotifyUrl;
    }

    public void setPersistentNotifyUrl(String persistentNotifyUrl) {
        this.persistentNotifyUrl = persistentNotifyUrl;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Integer getInstant() {
        return instant;
    }

    public void setInstant(Integer instant) {
        this.instant = instant;
    }

    public String getSaveKey() {
        return saveKey;
    }

    public void setSaveKey(String saveKey) {
        this.saveKey = saveKey;
    }

    public Long getSeparate() {
        return separate;
    }

    public void setSeparate(Long separate) {
        this.separate = separate;
    }

    @Override
    public String toString() {
        return "PutPolicy{" +
                "scope='" + scope + '\'' +
                ", deadline='" + deadline + '\'' +
                ", returnBody='" + returnBody + '\'' +
                ", overwrite=" + overwrite +
                ", fsizeLimit=" + fsizeLimit +
                ", returnUrl='" + returnUrl + '\'' +
                ", callbackUrl='" + callbackUrl + '\'' +
                ", callbackBody='" + callbackBody + '\'' +
                ", persistentOps='" + persistentOps + '\'' +
                ", persistentNotifyUrl='" + persistentNotifyUrl + '\'' +
                ", instant='" + instant + '\'' +
                ", saveKey='" + saveKey + '\'' +
                ", separate='" + separate + '\'' +
                '}';
    }

}


