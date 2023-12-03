package edoserver

type EdoServer struct {
	quicServer QuicServer
}

func (e *EdoServer) Start() error {
	err := e.quicServer.Start()
	if err != nil {
		return err
	}
	return nil
}
