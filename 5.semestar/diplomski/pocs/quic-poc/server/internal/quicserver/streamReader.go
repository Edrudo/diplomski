package quicserver

import (
	"io"
)

type StreamReader struct{ io.Reader }
