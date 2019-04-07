package cn.geosprite.eosprocess.service;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @ Author     ：wanghl
 * @ Date       ：Created in 10:47 2019-4-7
 * @ Description：None
 * @ Modified By：
 */
@Service
public class DataPreProcess {

    public void unzip(String input,String output) throws IOException {
        File destination = new File(output);
        File source = new File(input);

        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        archiver.extract(source,destination);
    }
}
