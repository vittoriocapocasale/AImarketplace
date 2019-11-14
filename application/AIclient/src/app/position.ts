//interface class to exchange data with the server

export class Position
{
    public readonly maxLatitude:number=90.00;
    public readonly minLatitude:number=-90.00;
    public readonly maxLongitude:number=180.00;
    public readonly minLongitude:number=-180.00;
    constructor (public  latitude:number,public longitude:number, public mark:number, public positionOwner:string){}
    //checks boundaries
    isvalid():boolean
    {
        return this.longitude < this.maxLongitude && this.longitude > this.minLongitude &&
               this.latitude < this.maxLatitude && this.latitude > this.minLatitude && this.mark>0;
    }
}
