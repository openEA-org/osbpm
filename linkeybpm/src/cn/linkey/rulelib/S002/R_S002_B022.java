package cn.linkey.rulelib.S002;

import java.io.*;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;

import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscoderException;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.util.Vml2Svg;

public class R_S002_B022 implements LinkeyRule {

	@Override
	public String run(HashMap<String, Object> params) throws Exception {
		//params为运行本规则时所传入的参数
		String processid = BeanCtx.g("Processid", true);
		if (Tools.isBlank(processid)) {
			return "";
		}
		String ToSvgHead = "<?xml version=\"1.0\" standalone=\"no\"?>\n";
		String sql = "select GraphicBody from BPM_ModGraphicList where Processid='" + processid + "'";
		String xmlBody = Rdb.getValueBySql(sql);
		xmlBody = Rdb.deCode(xmlBody, false);
		if (xmlBody.indexOf("<v:") > -1) {
			xmlBody = Vml2Svg.getSvgXml(xmlBody);
		} else {
			ToSvgHead = ToSvgHead + "<svg id=\"svg\" style=\"background-color: rgb(248, 248, 248);\" width=\"1160"
					+ "\" height=\"" + "930\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">\n";
			xmlBody = ToSvgHead + xmlBody;
			xmlBody += "\n</svg>";
		}
		String filePath = AppUtil.getPackagePath() + "process/" + processid + ".jpg";
		convertToPng(xmlBody, filePath);
		downloadFile(filePath, processid + ".jpg");

		return "";
	}

	public void downloadFile(String fullfilepath, String downloadfilename) throws Exception {
		/* 读取文件 */
		File file = new File(fullfilepath);
		/* 如果文件存在 */
		if (file.exists()) {
			BeanCtx.getResponse().reset();
			BeanCtx.getResponse().addHeader("Content-Disposition", "attachment; filename=\"" + downloadfilename + "\"");
			BeanCtx.getResponse().setContentType("application/x-msdownload");
			int fileLength = (int) file.length();
			BeanCtx.getResponse().setContentLength(fileLength);
			/* 如果文件长度大于0 */
			if (fileLength != 0) {
				/* 创建输入流 */
				InputStream inStream = new FileInputStream(file);
				byte[] buf = new byte[4096];
				/* 创建输出流 */
				ServletOutputStream servletOS = BeanCtx.getResponse().getOutputStream();
				int readLength;
				while (((readLength = inStream.read(buf)) != -1)) {
					servletOS.write(buf, 0, readLength);
				}
				inStream.close();
				servletOS.flush();
				servletOS.close();
			}
		} else {
			BeanCtx.print("Error: can't found the file " + fullfilepath);
		}
	}

	public static void convertToJpg(String InFilePath, String OutFilePath) throws IOException, TranscoderException {
		//svg文件转换成为jpg文件
		// Create a JPEG transcoder
		JPEGTranscoder t = new JPEGTranscoder();

		// Set the transcoding hints.
		t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1));

		// Create the transcoder input.
		String svgURI = new File(InFilePath).toURI().toString();
		TranscoderInput input = new TranscoderInput(svgURI);

		// Create the transcoder output.
		OutputStream ostream = new FileOutputStream(OutFilePath);
		TranscoderOutput output = new TranscoderOutput(ostream);

		// Save the image.
		t.transcode(input, output);

		// Flush and close the stream.
		ostream.flush();
		ostream.close();
	}

	public static void convertToPng(String SvgXmlCode, String pngFilePath) throws IOException, TranscoderException {
		//svg xml 字符串转换到指定的路径png文件
		File file = new File(pngFilePath);

		FileOutputStream outputStream = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			convertToPng(SvgXmlCode, outputStream);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void convertToPng(String svgCode, OutputStream outputStream) throws TranscoderException, IOException {
		try {
			byte[] bytes = svgCode.getBytes("utf-8");
			//20190715 修改为JPG图片
			JPEGTranscoder t = new JPEGTranscoder();
			t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 0.99f);
			//PNGTranscoder t = new PNGTranscoder();
			TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(bytes));
			TranscoderOutput output = new TranscoderOutput(outputStream);
			t.transcode(input, output);
			outputStream.flush();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}