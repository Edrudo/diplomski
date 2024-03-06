package config

var Cfg Config

type Config struct {
	ServerConfig Http3ServerConfig
	OuicConfig   QuicConfig
}

type Http3ServerConfig struct {
	Http3ServerUrl  string `split_words:"true" required:"true" default:"localhost"`
	Http3ServerPort int    `split_words:"true" required:"true" default:"4242"`
}
type QuicConfig struct {
	HandshakeIdleTimeoutMs uint `split_words:"true"`
	MaxIdleTimeoutMs       uint `split_words:"true"`
	KeepAlivePeriod        uint `split_words:"true"`
}
