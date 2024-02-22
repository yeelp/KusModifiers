package yeelp.kusmodifiers;

import java.io.PrintStream;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModConsts.MODID, name = ModConsts.NAME, version = ModConsts.VERSION, dependencies = "required-after:tconstruct@[1.12.2-2.133.0.183,)")
public final class KusModifiers {

	private static Logger logger;
	private static final String LOGGER_DEBUG_PREFIX = String.format("[%s (DEBUG)]", ModConsts.NAME.toUpperCase());
	private static final String LOGGER_PREFIX = String.format("[%s]", ModConsts.NAME.toUpperCase());

	private enum LoggerLevel {
		@SuppressWarnings("synthetic-access")
		DEBUG(LOGGER_DEBUG_PREFIX, Logger::info, System.out),
		INFO(Logger::info),
		WARN(Logger::warn),
		ERR(Logger::error, System.err),
		FATAL(Logger::fatal, System.err);

		private final String prefix;
		private final BiConsumer<Logger, String> logMethod;
		private final Consumer<String> fallback;

		private LoggerLevel(String prefix, BiConsumer<Logger, String> logMethod, PrintStream fallback) {
			this.prefix = prefix;
			this.logMethod = (logger, string) -> logMethod.accept(logger, this.formatString(string));
			this.fallback = (string) -> fallback.println(this.formatString(string));
		}

		@SuppressWarnings("synthetic-access")
		private LoggerLevel(BiConsumer<Logger, String> logMethod) {
			this(LOGGER_PREFIX, logMethod, System.out);
		}

		@SuppressWarnings("synthetic-access")
		private LoggerLevel(BiConsumer<Logger, String> logMethod, PrintStream fallback) {
			this(LOGGER_PREFIX, logMethod, fallback);
		}

		private final String formatString(String s) {
			return String.format("%s %s", this.prefix, s);
		}

		final Consumer<String> getLogMethod(Logger logger) {
			return (s) -> this.logMethod.accept(logger, s);
		}

		final Consumer<String> getFallbackMethod() {
			return this.fallback;
		}
	}

	@Instance(ModConsts.MODID)
	public static KusModifiers instance;

	@SuppressWarnings("static-method")
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}

	@SuppressWarnings("static-method")
	@EventHandler
	public void init(FMLInitializationEvent event) {

	}

	public static void info(String msg) {
		printWithLogger(msg, LoggerLevel.INFO);
	}

	public static void warn(String msg) {
		printWithLogger(msg, LoggerLevel.WARN);
	}

	public static final void err(String msg) {
		printWithLogger(msg, LoggerLevel.ERR);
	}

	public static final void fatal(String msg) {
		printWithLogger(msg, LoggerLevel.FATAL);
	}

	public static final void debug(String msg) {
		printWithLogger(msg, LoggerLevel.DEBUG);
	}

	private static final void printWithLogger(String msg, LoggerLevel logLevel) {
		getLogger().map(logLevel::getLogMethod).orElse(logLevel.getFallbackMethod()).accept(msg);
	}

	private static final Optional<Logger> getLogger() {
		return Optional.ofNullable(logger);
	}
}
