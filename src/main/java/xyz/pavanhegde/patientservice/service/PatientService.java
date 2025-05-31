package xyz.pavanhegde.patientservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import xyz.pavanhegde.patientservice.exception.PatientCreationException;
import xyz.pavanhegde.patientservice.exception.PatientNotFoundException;
import xyz.pavanhegde.patientservice.model.Patient;
import xyz.pavanhegde.patientservice.model.dto.PatientRequestDTO;
import xyz.pavanhegde.patientservice.model.dto.PatientResponseDTO;
import xyz.pavanhegde.patientservice.repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new PatientCreationException("A patient with email " +
                    patientRequestDTO.getEmail() + " already exists");
        }
        Patient patient = toModel(patientRequestDTO);
        return toResponse(patientRepository.save(patient));
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID " + id + " not found."));
        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new PatientCreationException("A patient with email " +
                    patientRequestDTO.getEmail() + " already exists");
        }
        patient.setFirstName(patientRequestDTO.getFirstName());
        patient.setLastName(patientRequestDTO.getLastName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setUpdatedDate(LocalDateTime.now());

        return toResponse(patientRepository.save(patient));
    }

    public void deletePatient(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with ID " + id + " not found."));
        patientRepository.delete(patient);
    }

    public PatientResponseDTO toResponse(Patient patient) {
        return PatientResponseDTO.builder()
                .id(patient.getId().toString())
                .name(patient.getFirstName() + " " + patient.getLastName())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .dateOfBirth(patient.getDateOfBirth().toString())
                .registeredDate(patient.getRegisteredDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .build();
    }

    public Patient toModel(PatientRequestDTO patientRequestDTO) {
        return Patient.builder()
                .firstName(patientRequestDTO.getFirstName())
                .lastName(patientRequestDTO.getLastName())
                .email(patientRequestDTO.getEmail())
                .address(patientRequestDTO.getAddress())
                .dateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()))
                .registeredDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }
}
