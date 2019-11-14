import {Position} from './position'
//interface class to exchange data with the server

export class Archive
{
    constructor (public  tagName:string, public owner:string, public price:number, public soldTimes:number, public positions:Array<Position>){}
}
