
@Entity
@Table(name = "events")
class Event {
    @Column(nullable = false)
    var title: String

    @Column(columnDefinition = "TEXT", nullable = true)
    var description: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "recurring_pattern", nullable = true)
    var recurringPattern: RecurringPattern? = null

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", referencedColumnName = "id")
    var calendar: Calendar? = null

    @Column(nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    var category: Category? = null
} : Entity()
