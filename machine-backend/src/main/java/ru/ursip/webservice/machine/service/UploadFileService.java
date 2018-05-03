package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * service for upload excelFiles
 *
 * @author samsonov
 * @since 31.01.2018
 */
public interface UploadFileService {
    /**
     * Download resource.cost_items.amortization.ave_price
     *
     * @param periodId - periodId
     * @param file     - file
     * @param fileLogDir     - file Log Directory
     * @return ResponseEntity
     */
    ResponseEntity avePrice(String periodId, MultipartFile file, String fileLogDir);

    /**
     * Download estimate prices
     *
     * @param periodId - periodId
     * @param file     - file
     * @param fileLogDir     - file Log Directory
     * @return ResponseEntity
     */
    ResponseEntity estimatePrice(String periodId, MultipartFile file, String fileLogDir);
}
