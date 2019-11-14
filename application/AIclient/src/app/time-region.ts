import {Position} from './position'
import { User } from './user';
//interface class to exchange data with the server
export class TimeRegion
{
    constructor (public from:number, public to:number, public shape:Array<Position>, public users:Array<string>){}
}
