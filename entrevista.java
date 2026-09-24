@Entity
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal monto;
    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado;
    private LocalDateTime fechaCreacion;
    @Version
    private Long version;
    // getters, setters y constructores omitidos por brevedad
}

public enum EstadoTransaccion {
    CREADA, PENDIENTE, APROBADA, RECHAZADA, REVERSADA
}

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
}

@Service
    public class TransaccionService {
    private final TransaccionRepository transaccionRepository;

    public TransaccionService(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    public void reversar(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transacción no encontrada"));
        if (transaccion.getEstado() != EstadoTransaccion.APROBADA) {
            throw new ConflictException("Solo se pueden reversar transacciones aprobadas");
        }
        if (transaction.fechaCreacion.isBefore(LocalDateTime.now().minusDays(1))) {
            throw new UnprocessableEntityException("No se pueden reversar transacciones con más de 1 días de antigüedad");
        }
        if (transaccion.getVersion() != null && transaccion.getVersion() > 0) {
            throw new OptimisticLockException("La transacción ya ha sido modificada por otra operación");
        }
        transaccion.setEstado(EstadoTransaccion.REVERSADA);
        transaccionRepository.save(transaccion);
    }
}
@RestController
@RequestMapping("/transacciones")
public class TransaccionController {
    private final TransaccionService transaccionService;
    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }
    @PatchMapping("/{id}/reversar")
        public ResponseEntity<Void> reversar(@PathVariable Long id) {
        transaccionService.reversar(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handleConflictException(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<String> handleUnprocessableEntityException(UnprocessableEntityException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<String> handleOptimisticLockException(OptimisticLockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}

class TransaccionServiceTest {
    private TransactionRepository transaccionRepository;
    private TransaccionService transaccionService = new TransaccionService(transaccionRepository);

    @Test 
    void reversar24HorasDespuesDeCreacion() {
        Transaccion transaccion = new Transaccion();
        transaccion.setEstado(EstadoTransaccion.APROBADA);
        transaccion.setFechaCreacion(LocalDateTime.now().minusDays(2));
        transaccionRepository.save(transaccion);

        assertThrows(UnprocessableEntityException.class, () -> {
            transaccionService.reversar(transaccion.getId());
        });
    }

    @Test
    void reversar24HorasAntesDeCreacion() {
        Transaccion transaccion = new Transaccion();
        transaccion.setEstado(EstadoTransaccion.APROBADA);
        transaccion.setFechaCreacion(LocalDateTime.now().minusHours(23));
        transaccionRepository.save(transaccion);

        assertDoesNotThrow(() -> {
            transaccionService.reversar(transaccion.getId());
        });
    }

    @Test 
    void reversarConEstadoPendiente() {
        Transaccion transaccion = new Transaccion();
        transaccion.setEstado(EstadoTransaccion.PENDIENTE);
        transaccion.setFechaCreacion(LocalDateTime.now().minusHours(23));
        transaccionRepository.save(transaccion);

        assertThrows(ConflictException.class, () -> {
            transaccionService.reversar(transaccion.getId());
        });
    }

    @Test 
    void reversarConEstadoAprobado() {
        Transaccion transaccion = new Transaccion();
        transaccion.setEstado(EstadoTransaccion.APROBADA);
        transaccion.setFechaCreacion(LocalDateTime.now().minusHours(23));
        transaccionRepository.save(transaccion);

        assertThrows(ConflictException.class, () -> {
            transaccionService.reversar(transaccion.getId());
        });
    }
}