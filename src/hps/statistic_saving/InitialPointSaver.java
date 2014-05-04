package hps.statistic_saving;

import hps.program_starter.HPS;
import hps.tools.AsyncOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InitialPointSaver {
	
	private File targetFolder;

	public InitialPointSaver() throws IOException, InterruptedException {
		targetFolder = new File(HPS.get().getOutputsFolder().getPath() + "/settings");
		if (!targetFolder.exists() || !targetFolder.isDirectory())
			targetFolder.mkdirs();
	}
	
	public void save(String fileName, String fileContent) throws IOException, InterruptedException {
		File file = new File(String.format("%s/%s", targetFolder.getPath(), fileName));
		file.createNewFile();
		FileOutputStream fout = new FileOutputStream(file);
		AsyncOutputStream afout = new AsyncOutputStream(new BufferedOutputStream(fout));
		afout.write(fileContent.getBytes());
		afout.close();
	}
	
	public void saveForCurrentPoint(String habitat, String fileName, String fileContent) throws IOException, InterruptedException {
		File pointFolder = new File(String.format("%s/%s/%s",
				targetFolder.getPath(),
				HPS.get().getCurrentPointName(),
				habitat));
		if (!pointFolder.exists())
			pointFolder.mkdirs();
		File file = new File(String.format("%s/%s", pointFolder.getPath(), fileName));
		file.createNewFile();
		FileOutputStream fout = new FileOutputStream(file);
		AsyncOutputStream afout = new AsyncOutputStream(new BufferedOutputStream(fout));
		afout.write(fileContent.getBytes());
		afout.close();
	}
}
