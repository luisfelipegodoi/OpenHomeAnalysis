package br.com.brolam.oha.supervisory.data.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import br.com.brolam.library.helpers.OhaHelper;
import static br.com.brolam.oha.supervisory.data.OhaEnergyUseContract.*;

/**
 * Gerencia um banco de dados local para dados do supervisory.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class OhaSQLHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 21;
    public static final String DATABASE_NAME = "supervisory.db";
    public static final String BACKUP_DIRECTORY = "Oha/Backups";

    public OhaSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //EnergyUseLogEntry
        sqLiteDatabase.execSQL(EnergyUseLogEntry.getSQLCreate());
        //EnergyUseBillEntry
        sqLiteDatabase.execSQL(EnergyUseBillEntry.getSQLCreate());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (newVersion) {}
    }

    /**
     * Realizar o backup do banco de dados compactado.
     * @param context informar um contexto válido.
     * @param backupName somente o nome do backup
     * @param iZipFile {@link br.com.brolam.library.helpers.OhaHelper.IZipFile}
     * @throws IOException
     */
    public static void backup(Context context, String backupName, OhaHelper.IZipFile iZipFile) throws IOException {
        File sd = new File(Environment.getExternalStorageDirectory(),BACKUP_DIRECTORY);
        sd.mkdirs();
        File data = Environment.getDataDirectory();
        if (sd.canWrite()) {
            String dataBasePath = String.format("//data//%s//databases//%s", context.getPackageName(), DATABASE_NAME);
            File fileDataBase = new File(data, dataBasePath);
            if (fileDataBase.exists() ) {
                OhaHelper.zipFile(fileDataBase, String.format("%s/%s.zip", sd.getPath(), backupName), iZipFile);
            }
        }
    }

    /**
     * Realizar a restouração do banco de dados.
     * @param context informar um constexto válido.
     * @param backupNamePath informar o caminho completo do backup.
     * @param iZipFile {@link br.com.brolam.library.helpers.OhaHelper.IZipFile}
     * @throws IOException
     */
    public static void restore(Context context, String backupNamePath, OhaHelper.IZipFile iZipFile) throws IOException {
        String dataBasePath = String.format("//data//%s//databases//", context.getPackageName());
        File data = Environment.getDataDirectory();
        File directoryDataBase = new File(data, dataBasePath);
        OhaHelper.unZipFile(backupNamePath, DATABASE_NAME, directoryDataBase.getPath(), iZipFile);
    }

    /**
     * Recuperar uma lista com os backups do banco de dados.
     */
    public static File[] getBackups() {
        //recuperar somente os arquivos com extenção zip:
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().toLowerCase().contains("zip");
            }
        };
        File backupsPath = new File(Environment.getExternalStorageDirectory(), BACKUP_DIRECTORY);
        return backupsPath.listFiles(fileFilter);
    }

}
