import java.util.Properties;

import com.lswe.net.UpdateApkServlet;

public class UpdateApkServlet {
	// ��ȡ��Դ�ļ���Ϣ
	static {
		Properties ppt = new Properties();
		try {
			ppt.load(UpdateApkServlet.class
					.getResourceAsStream("/apkVersion.properties"));
			apkVersion = ppt.getProperty("apkVersion");
			apkSize = ppt.getProperty("apkSize");
			apkPath = ppt.getProperty("apkPath");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}