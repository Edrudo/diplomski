export interface IComparable<T> {
    compare(other: T) : number
}

// export interface IHasNorm {
//     norm() : number
// }

export type IHasNorm  = {
    get norm() : number
}
