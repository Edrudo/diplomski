package config

var Cfg Config

type Config struct {
	App          AppConfig         `split_words:"true" required:"true"`
	ServerConfig Http3ServerConfig `split_words:"true" required:"true"`
	QuicConfig   QuicConfig        `split_words:"true" required:"true"`
}

// AppConfig is a struct that contains application's full name and namespace.
type AppConfig struct {
	FullName  string `split_words:"true" required:"true"`
	Namespace string `split_words:"true" required:"true"`
}

type Http3ServerConfig struct {
	Http3ServerUrl  string `split_words:"true" required:"true"`
	Http3ServerPort int    `split_words:"true" required:"true"`
}
type QuicConfig struct {
	HandshakeIdleTimeoutMs int `split_words:"true" required:"true"`
	MaxIdleTimeoutMs       int `split_words:"true" required:"true"`
	KeepAlivePeriod        int `split_words:"true" required:"true"`
}
