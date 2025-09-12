package com.chinanetcenter.api.util;

import com.chinanetcenter.api.entity.PutPolicy;

import java.util.HashMap;
import java.util.Map;

/**
 * Upload Policy Tool Class
 */
public class PutPolicyUtil {

    /**
     * Parse the input parameters<br>
     * The command is, for example: `java -jar wcs-demo-1.0-SNAPSHOT-jar-with-dependencies.jar put testbucket0001 panda002.mp4 D:\Project\WCSdocs\对外接口资料\test.mp4 persistentOps/avthumb/flv/vb/1.25m;vframe/jpg/offset/0`<br>
     * The parameter `persistentOps/avthumb/flv/vb/1.25m;vframe/jpg/offset/0` will be parsed into two parts: `persistentOps/` and `avthumb/flv/vb/1.25m;vframe/jpg/offset/0`.<br>
     * The `persistentOps` attribute in the final `PutPolicy` object will be set to `avthumb/flv/vb/1.25m;vframe/jpg/offset/0`.<br>
     * The parsing of other parameters of PutPolicy is similar to the above example.<br>
     * @param args
     * @return PutPolicy object
     */
    public static PutPolicy setArgs(String[] args) {
        PutPolicy putPolicy = new PutPolicy();
        String[] tmpstr;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("size")) {
                tmpstr = args[i].split("size=");
                putPolicy.setFsizeLimit(Long.parseLong(tmpstr[1]));
            } else if (args[i].startsWith("overwrite")) {
                tmpstr = args[i].split("overwrite=");
                putPolicy.setOverwrite(Integer.parseInt(tmpstr[1]));
            } else if (args[i].startsWith("returnUrl")) {
                tmpstr = args[i].split("returnUrl=");
                putPolicy.setReturnUrl(tmpstr[1]);
            } else if (args[i].startsWith("e")) {//Calculated by the second
                tmpstr = args[i].split("e=");
                putPolicy.setDeadline(tmpstr[1]);
            } else if (args[i].startsWith("returnBody")) {
                tmpstr = args[i].split("returnBody=");
                putPolicy.setReturnBody(tmpstr[1]);
            } else if (args[i].startsWith("callbackUrl")) {
                tmpstr = args[i].split("callbackUrl=");
                putPolicy.setCallbackUrl(tmpstr[1]);
            } else if (args[i].startsWith("callbackBody")) {
                tmpstr = args[i].split("callbackBody=");
                putPolicy.setCallbackBody(tmpstr[1]);
            } else if (args[i].startsWith("persistentOps")) {
                tmpstr = args[i].split("persistentOps=");
                putPolicy.setPersistentOps(tmpstr[1]);
            } else if (args[i].startsWith("persistentNotifyUrl")) {
                tmpstr = args[i].split("persistentNotifyUrl=");
                putPolicy.setPersistentNotifyUrl(tmpstr[1]);
            }
        }
        return putPolicy;
    }

    public static Map<String,String> setXArgs(String[] args){
        Map<String,String> params = new HashMap<String, String>();
        String[] tmpstr;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("x:")) {
                tmpstr = args[i].split("=");
                params.put(tmpstr[0],tmpstr[1]);
            }
        }
        return params;
    }

}
