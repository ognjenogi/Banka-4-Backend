import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

abstract class MakeMigrationTask @Inject constructor(
	private val objects: ObjectFactory,
) : DefaultTask() {
	@get:Input
	@Option(
		option = "name",
		description = "Descriptive name for the new migration.  Must be alphanumeric or underline"
	)
	val migrationName: Property<String> = objects.property()

	@get:OutputDirectory
	abstract val outputDirectory: DirectoryProperty

	@TaskAction
	fun action() {
		val currentTime = OffsetDateTime.now(ZoneOffset.UTC);
		val currentTimeStr = currentTime.format(
			DateTimeFormatter.ofPattern("YYYY'.2'MM'.'dd")
		)
		val migName = migrationName.get()
		if (!migName.matches("[a-zA-Z0-9_]+".toRegex()))
			throw GradleException(
				"Migration name must consist of alphanumerics and underlines only"
			)
		val fileName = "V$currentTimeStr.${currentTime.toEpochSecond()}__$migName.sql";

		val outDir = outputDirectory.get()
		outDir.asFile.mkdirs()
		val migrationPath = outDir.file(fileName).asFile.toPath()
		Files.write(
			migrationPath,
			listOf(
				"-- Put your migration here.  The following line fails intentionally",
				"SELECT 1 + 'foo';",
			),
			StandardOpenOption.CREATE_NEW
		)
		logger.quiet("Generated migration file {}", migrationPath.toAbsolutePath())
	}
}

tasks.register<MakeMigrationTask>("makeMigration") {
	outputs.upToDateWhen { false }
	outputDirectory.set(
		// TODO(arsen): BAD!
		project.projectDir.resolve("src/main/resources/db/migration")
	)
}
