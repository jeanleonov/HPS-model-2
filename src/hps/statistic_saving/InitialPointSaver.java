package hps.statistic_saving;

import hps.tools.CMDArgument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class InitialPointSaver {
	
	private File targetFolder;
	private ZipOutputStream zippedPointInfo;

	public InitialPointSaver() throws IOException {
		targetFolder = new File((String)CMDArgument.SETTINGS_FOLDER.getValue());
		if (!targetFolder.exists() || !targetFolder.isDirectory())
			targetFolder.createNewFile();
	}
	
	public void openPoint(String pointNumber) throws IOException {
		FileOutputStream fout = new FileOutputStream(targetFolder.getPath() + File.separator + pointNumber);
		zippedPointInfo = new ZipOutputStream(fout);
	}
	
	public void save(String fileName, String fileContent) throws IOException {
		zippedPointInfo.putNextEntry(new ZipEntry(fileName));
		zippedPointInfo.write(fileContent.getBytes());
		zippedPointInfo.closeEntry();
	}
	
	public void closePoint() throws IOException {
		zippedPointInfo.flush();
		zippedPointInfo.close();
	}
}
