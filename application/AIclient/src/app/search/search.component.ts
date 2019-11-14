import { Component, OnInit } from '@angular/core';
import {LatLng, LatLngBounds } from 'leaflet';
import {Position} from '../position'
import { PositionService } from '../position.service';
import { User } from '../user';
import {TimeRegion} from '../time-region'
import { IdentityService } from '../identity.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  constructor(private positionService:PositionService, private identityService:IdentityService, private router:Router) { }

  polygon:Array<LatLng>=[]
  bounds:Array<LatLng>=[]
  point:LatLng;
  latLngPositions:Position[]=[];
  timestampPositions:Position[]=[];
  users:string[]=[];
  selectedUsers:string[]=[];
  availablePositions:number=0;
  isPolygon=false;
  fromDate: Date = new Date();
  toDate: Date = new Date();
  settings = {
        bigBanner: true,
        timePicker: true,
        format: 'dd-MM-yyyy hh:mm',
        defaultOpen: false
    }
  ngOnInit() {
    this.identityService.getUsernames().subscribe(e=>this.users=e);
  }

  //if point is inside polygon, removes the polygon, otherwise add a vertex to the polygon
  onMapClicked(point:LatLng)
  {
    if(this.isLatLngInsidePolygon(point, this.polygon))
    {
      this.polygon=[];
      this.point=null;
    }
    else
    {
      this.polygon.push(point);
      this.point=point;
    }
    if(this.polygon.length>2)
    {
      this.isPolygon=true;
    }
    else{
      this.isPolygon=false;
    }
  }

  //called when map bounds change
  updateBounds (bounds:LatLngBounds)
  {
    this.bounds=[];
    this.bounds.push(bounds.getNorthEast());
    this.bounds.push(bounds.getSouthEast());
    this.bounds.push(bounds.getSouthWest());
    this.bounds.push(bounds.getNorthWest());
    console.log(bounds);
  }

  //button to select all users
  selectAll()
  {
    let names:string[]=[];
    this.users.forEach(e=>names.push(e));
    this.selectedUsers=names;
  }

  //button to select no user
  selectNone()
  {
    this.selectedUsers=[];
  }

  //called when a new from date is inserted
  onFromDateSelect(event)
  {
    let d:Date=new Date(event);
    let now= new Date();
    let zero= new Date(0);
    if(d.getTime()>zero.getTime()&&d.getTime()<=now.getTime())
    {
      this.fromDate=d;
    }
    else
    {
      this.fromDate=zero;
    }
  }

  //calle dwhen a new to date is inserted
  onToDateSelect(event)
  {
    let d:Date=new Date(event);
    let now= new Date();
    let zero= new Date(0);
    if(d.getTime()>zero.getTime()&&d.getTime()<=now.getTime())
    {
      this.toDate=d;
    }
    else
    {
      this.toDate=now;
    }
  }

  //retrieves from server curren informations about positions
  updateView(){
    let points:Position[]=[];
    if(this.polygon.length>2)
    {
      this.polygon.forEach(e=>points.push(new Position(e.lat, e.lng,0,"")));
    }
    else
    {
      this.bounds.forEach(e=>points.push(new Position(e.lat, e.lng,0,"")));
    }
    let tr:TimeRegion = new TimeRegion(this.fromDate.getTime(), this.toDate.getTime(), points, this.selectedUsers);
    this.positionService.searchTimestampPositions(tr).subscribe(e=>{if(e){this.timestampPositions=e;}console.log(e), console.log(points)});
    this.positionService.searchLatLngPositions(tr).subscribe(e=>{if(e){this.latLngPositions=e;}console.log(e)});
    this.positionService.searchNumberPositions(tr).subscribe(e=>{if(e){this.availablePositions=e;}console.log(e)});
    this.identityService.getUsernames().subscribe(e=>{if(e){this.users=e}console.log(e)});

  }


  //ask the server for the archives containing the selected positions
  continueToPurchase()
  {
    let points:Position[]=[];
    if(this.polygon.length>2)
    {
      this.polygon.forEach(e=>points.push(new Position(e.lat, e.lng,0,"")));
    }
    else
    {
      this.bounds.forEach(e=>points.push(new Position(e.lat, e.lng,0,"")));
    }

    let tr:TimeRegion = new TimeRegion(this.fromDate.getTime(), this.toDate.getTime(), points, this.selectedUsers);
    this.positionService.searchArchives(tr).subscribe(e=>{
      if(e){
        this.positionService.setSelectedArchives(e);
      }
      else{
        this.positionService.setSelectedArchives([]);
      }
      this.router.navigate([`/login/${this.identityService.getCurrentUserRole()}/${this.identityService.getCurrentUsername()}/purchase`]);});
  }
  //https://stackoverflow.com/questions/31790344/determine-if-a-point-reside-inside-a-leaflet-polygon
  isLatLngInsidePolygon(marker:LatLng, poly:Array<LatLng>) {
    let polyPoints = poly;
    let x = marker.lat
    let y = marker.lng;
    let inside = false;
    for (let i = 0, j = polyPoints.length - 1; i < polyPoints.length; j = i++) {
        let xi = polyPoints[i].lat;
        let yi = polyPoints[i].lng;
        let xj = polyPoints[j].lat;
        let yj = polyPoints[j].lng;

        let intersect = ((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
        if (intersect)
        {
          inside = !inside;
        }
    }

    return inside;
};

}
