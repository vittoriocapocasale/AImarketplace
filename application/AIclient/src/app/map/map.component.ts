import { Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {LatLng, latLng, tileLayer,  Map, Polygon, Circle, CircleMarker, CircleMarkerOptions, Layer, LatLngBounds} from 'leaflet'
import {Position} from '../position'
import { RadioControlValueAccessor } from '@angular/forms';
@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {

  options:any = {
    zoom: 5,
    center: latLng([ 41.879966, 12.496909 ])
  };
  zoom:number=5;
  center:LatLng= new LatLng(41.879966, 12.496909);
  maxZoom:number =15;
  minZoom:number =6;
  map:Map=null;
  _polygon:Polygon=new Polygon([]);
  _positions:Circle[]=[];
  layer:Layer= tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  });

  @Input()//shows the passed position in the map
  set positions(positions:Position[]){
    if(this.map==null)
    {
      return;
    }
    for (let i=0; i< this._positions.length; i++)
    {
      this._positions[i].removeFrom(this.map);
    }
    this._positions=[];

    for(let i=0; i<positions.length; i++)
    {
      let color=this.colorHasher(positions[i].positionOwner)
      let options:CircleMarkerOptions = {
        color: color,
        fillColor: color,
        fillOpacity: 0.5,
        radius: 10*this.zoom*this.zoom
      }
      let c=new Circle(new LatLng(positions[i].latitude, positions[i].longitude),options);
      c.addTo(this.map);
      this._positions.push(c);

    }
  }

  //shows the passed polygon in the map
  @Input()
  set polygon(value:LatLng|null) {
    if(!(this.map===null))
    {
      if (value==null)
      {
        this._polygon.removeFrom(this.map);
        this._polygon=new Polygon([]);
        this._polygon.addTo(this.map);
      }
      else{
        this._polygon.addLatLng(value);
      }
    }
  }





  //notifies maps clicks
  @Output()
  private mouseClick$:EventEmitter<LatLng>=new EventEmitter();

  //notifies map bounds changes
  @Output()
  private bounds$:EventEmitter<LatLngBounds> =new EventEmitter();


  constructor() { }

  ngOnInit() {
  }

  onMapReady(map:Map){
    this.map=map;
    this.layer.addTo(this.map);
    this._polygon.addTo(this.map);
    this.bounds$.emit(map.getBounds());
  }

  mouseClicked(event)
  {
    this.mouseClick$.emit(event.latlng);
  }

  publishBounds(event)
  {
    if(this.map)
    {
      this.bounds$.emit(this.map.getBounds());
    }
  }

  //hash function to show a different color based on user
  private colorHasher(s:string):string {
    let hash=17;
    for (let i = 0; i < s.length; i++) {
      hash = s.charCodeAt(i) + ((hash << 5) - hash);
      hash = hash & hash;
    }
    let color = '#';
    for (let i = 0; i < 3; i++) {
        let value = (hash >> (i * 8)) & 255;
        color += ('00' + value.toString(16)).substr(-2);
    }
    return color;
  }

}
