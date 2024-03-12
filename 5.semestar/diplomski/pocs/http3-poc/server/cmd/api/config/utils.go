package config

import (
	"fmt"
	"os"
	"strings"

	"github.com/joho/godotenv"
	"github.com/kelseyhightower/envconfig"
	"github.com/smartystreets/goconvey/convey"
)

func Load() error {
	return LoadConfig(&Cfg, &Cfg.App)
}

// LoadConfig loads the configuration from environment variables.
// It also sets AppConfig Index and Name if necessary.
// The configuration argument must not be nil.
// appConfig argument can be nil, in which case Index and Name will not be set.
func LoadConfig(cfg interface{}, appConfig *AppConfig) error {
	err := envconfig.Process("", cfg)
	if err != nil {
		return err
	}
	return nil
}

// LoadConfigForTest is similar to LoadConfig, except this one is used for testing.
// It creates tests data using .env file.
func LoadConfigForTest(cfg interface{}, appConfig *AppConfig, dotEnvPath string) error {
	envSnapshot := snapshotEnvironmentVariables()

	// This will make tests deterministic since otherwise it would skip the variables that were
	// already configured as environment variables (via shell).
	os.Clearenv()

	// Load environment variables for this process
	err := godotenv.Load(dotEnvPath)
	if err != nil {
		return err
	}

	err = LoadConfig(cfg, appConfig)
	restoreEnvironmentVariables(envSnapshot)
	return err
}

// ShouldResemble is a function used for configuration testing.
// It compares two configurations and returns an error message if
// the two configurations do not resemble each other.
func ShouldResemble(actual interface{}, expected ...interface{}) string {
	errorMessage := convey.ShouldResemble(actual, expected[0])
	if errorMessage == "" {
		return ""
	}
	customMessage := `
	+--------------------------------------------------------------------------+
	| You have CHANGED THE ENVIRONMENT!                                        |
	|                                                                          |
	| If change effects how the service behaves, before fixing this tests,      |
	| make sure to create appropriate task for updating K8S deployments.       |
	+--------------------------------------------------------------------------+
`
	return fmt.Sprintf("%s\n\n%s", errorMessage, customMessage)
}

func snapshotEnvironmentVariables() []string {
	return os.Environ()
}

func restoreEnvironmentVariables(vars []string) {
	os.Clearenv()
	for _, e := range vars {
		pair := strings.Split(e, "=")
		os.Setenv(pair[0], pair[1])
	}
}
