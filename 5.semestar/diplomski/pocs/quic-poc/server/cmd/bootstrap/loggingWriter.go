package bootstrap

import (
	"fmt"
	"io"
)

type LoggingWriter struct{ io.Writer }

func (w LoggingWriter) Write(b []byte) (int, error) {
	fmt.Printf("ServerConfig: Got '%s'\n", string(b))
	return w.Writer.Write(b)
}
