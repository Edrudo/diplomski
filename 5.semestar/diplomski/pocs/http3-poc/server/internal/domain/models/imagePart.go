package models

type ImagePart struct {
	ImageHash  string
	PartNumber int
	TotalParts int
	PartData   []byte
}
