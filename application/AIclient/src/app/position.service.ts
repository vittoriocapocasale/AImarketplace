import { Injectable, OnInit, OnDestroy } from '@angular/core';
import {Observable, from, Subscription} from 'rxjs'
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { IdentityService } from './identity.service';
import { Archive } from './archive';
import {UrlService} from './url.service'
import {TimeRegion} from './time-region'
import {Position} from './position'
var uriTemplates = require('uri-templates');

//consumens the api for position/archive related queries
@Injectable()
export class PositionService{
  constructor(private http: HttpClient, private urlService:UrlService, private identityService:IdentityService) {
    this.$user=this.identityService.getUserChanges().subscribe(this.selectedArchives=null)
   }

  selectedArchives:Array<string>=null;
  $user:Subscription;


  publishArchive(archive:Archive):Observable<Archive>
  {
    let httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    let urlTemplate = uriTemplates(this.urlService.getUrl('soldArchives'));
    let url=urlTemplate.fill({ user:this.identityService.getCurrentUsername()});
    console.log(url);
    return this.http.post<Archive>(url, archive, httpOptions);
  }

  loadBoughtArchivesNames():Observable<Array<Archive>>
  {
    let urlTemplate = uriTemplates(this.urlService.getUrl('boughtArchives'));
    let url=urlTemplate.fill({user:this.identityService.getCurrentUsername()});
    console.log(url);
    return this.http.get<Array<Archive>>(url);
  }

  buyArchives(archives:Array<string>):Observable<void>
  {
    let httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    let urlTemplate = uriTemplates(this.urlService.getUrl('boughtArchives'));
    let url=urlTemplate.fill({user:this.identityService.getCurrentUsername()});
    console.log(url);
    return this.http.patch<void>(url, archives, httpOptions);
  }

  loadBoughtArchive(archive:string):Observable<Archive>
  {
    let urlTemplate = uriTemplates(this.urlService.getUrl('boughtArchive'));
    let url=urlTemplate.fill({boughtArchive: archive, user:this.identityService.getCurrentUsername()});
    console.log(url);
    return this.http.get<Archive>(url);
  }

  loadSoldArchivesNames():Observable<Array<Archive>>
  {
    let urlTemplate = uriTemplates(this.urlService.getUrl('soldArchives'));
    let url=urlTemplate.fill({user:this.identityService.getCurrentUsername()});
    return this.http.get<Array<Archive>>(url);
  }

  loadSoldArchive(archive:string):Observable<Archive>
  {
    let urlTemplate = uriTemplates(this.urlService.getUrl('soldArchive'));
    let url=urlTemplate.fill({soldArchive: archive, user:this.identityService.getCurrentUsername()});
    return this.http.get<Archive>(url);
  }

  deleteSoldArchive(archive:string):Observable<void>
  {
    let urlTemplate = uriTemplates(this.urlService.getUrl('soldArchive'));
    let url=urlTemplate.fill({soldArchive: archive, user: this.identityService.getCurrentUsername()});
    return this.http.delete<void>(url);
  }

  searchTimestampPositions(timeRegion:TimeRegion):Observable<Array<Position>>
  {
    let filters= encodeURIComponent(JSON.stringify(timeRegion));
    let urlTemplate = uriTemplates(this.urlService.getUrl('positions'));
    let url=urlTemplate.fillFromObject({params: {type: "timestamp", filter: filters}});
    console.log(url);
    return this.http.get<Array<Position>>(url);
  }

  searchLatLngPositions(timeRegion:TimeRegion):Observable<Array<Position>>
  {
    let filters= encodeURIComponent(JSON.stringify(timeRegion));
    let urlTemplate = uriTemplates(this.urlService.getUrl('positions'));
    let url=urlTemplate.fillFromObject({params: {type: "latlng", filter: filters}});
    console.log(url);
    return this.http.get<Array<Position>>(url);
  }

  searchNumberPositions(timeRegion:TimeRegion):Observable<number>
  {
    let filters= encodeURIComponent(JSON.stringify(timeRegion));
    let urlTemplate = uriTemplates(this.urlService.getUrl('count'));
    let url=urlTemplate.fillFromObject({params: {filter: filters}});
    console.log(url);
    return this.http.get<number>(url);
  }

  searchArchives(timeRegion:TimeRegion):Observable<Array<string>>
  {
    let filters= encodeURIComponent(JSON.stringify(timeRegion));
    let urlTemplate = uriTemplates(this.urlService.getUrl('archives'));
    let url=urlTemplate.fillFromObject({params: {filter: filters}});
    console.log(url);
    return this.http.get<Array<string>>(url);
  }

  setSelectedArchives(archives:Array<string>):void
  {
    this.selectedArchives=archives;
  }
  getSelectedArchives():Array<string>
  {
    return this.selectedArchives;
  }

  downloadFile(data: Archive) {
    let positions:any=[];
    data.positions.forEach(e=>{positions.push({
      latitude: e.latitude,
      longitude: e.longitude,
      mark: new Date(e.mark),
    });})
    var sJson = JSON.stringify(positions);
    var element = document.createElement('a');
    element.setAttribute('href', "data:text/json;charset=UTF-8," + encodeURIComponent(sJson));
    element.setAttribute('download', data.tagName+".json");
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click(); // simulate click
    document.body.removeChild(element);
  }
}
