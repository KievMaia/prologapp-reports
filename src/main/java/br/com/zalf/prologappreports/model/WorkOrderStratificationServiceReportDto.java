package br.com.zalf.prologappreports.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder(toBuilder = true, setterPrefix = "with")
public class WorkOrderStratificationServiceReportDto {
    final Long workOrderPerformedId = 1L;
    final Long serviceId = 10L;
    final String serviceName = "Test report";
    final String additionalId = "123";
    final String parentName = "Test report";
    final BigDecimal serviceCost = BigDecimal.TEN;
    final Double resolutionCost = 10D;
    final String branchOfficeName = "Test report";
    final String licensePlate = "Test 123";
    final String fleetId = "123";
    final String vehicleType = "Test report";
    final Long workOrderIdentifier = 1L;
    final Long issueId = 1L;
    final String issueTitle = "Test report";
    final String issueDescription = "Test report";
    final Long supplierId = 1L;
    final String supplierName = "Test report";
    final String createdByUserName = "Test report";
    final String closedByUserName = "Test report";
    final Instant resolutionStartDate = Instant.now();
    final Instant resolutionEndDate = Instant.now();
    final Instant prologResolvedDate = Instant.now();
    final String resolutionNotes = "Test report";
    final Long resolutionOdometer = 1L;
}
