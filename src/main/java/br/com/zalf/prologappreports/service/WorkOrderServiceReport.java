package br.com.zalf.prologappreports.service;

import br.com.zalf.prologappreports.model.WorkOrderStratificationServiceReportDto;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WorkOrderServiceReport {
    public byte[] getReportDto() {
        try {
            final var inputStream =
                    getClass().getResourceAsStream("/relatorios/relatorio-de-servicos-realizados.jasper");

            final var parameters = new HashMap<String, Object>();
            parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));

            final var workOrderStratificationServiceReportDtos =
                    Collections.nCopies(100, WorkOrderStratificationServiceReportDto.builder().build());
            final var dataSource = new JRBeanCollectionDataSource(workOrderStratificationServiceReportDtos);

            final var jasperPrint = JasperFillManager.fillReport(inputStream, parameters, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

