import javax.annotation.processing.Generated

@MappedSuperClass
abstract class Entity {
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID();
}
