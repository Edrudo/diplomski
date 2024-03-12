package config_test

import (
	"testing"

	. "github.com/smartystreets/goconvey/convey"

	"http3-server-poc/cmd/api/config"
)

var expectedConfig = config.Config{
	App: config.AppConfig{
		FullName:  "server",
		Namespace: "master-thesis",
	},
	ServerConfig: config.Http3ServerConfig{
		Http3ServerUrl:  "http3-server-url",
		Http3ServerPort: 4219,
	},
	QuicConfig: config.QuicConfig{
		HandshakeIdleTimeoutMs: 1000,
		MaxIdleTimeoutMs:       1000,
		KeepAlivePeriod:        1000,
	},
}

func TestConfig(t *testing.T) {
	Convey(
		"Given Config and defined environment variables", t, func() {

			Convey(
				"The configuration should be loaded from the environment without errors", func() {

					err := config.LoadConfigForTest(&config.Cfg, &config.Cfg.App, "../../../.env")
					So(err, ShouldBeNil)

					Convey(
						"Then the constructed Config object should have all the values loaded", func() {

							Convey(
								"And you should have made sure that you updated K8S env", func() {
									So(config.Cfg, ShouldResemble, expectedConfig)
								},
							)
						},
					)
				},
			)
		},
	)
}
