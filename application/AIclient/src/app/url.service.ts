import { Injectable, EventEmitter, SystemJsNgModuleLoader } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from './../environments/environment';
import { hasMagic } from 'glob';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

//class to menage configuration and urls
@Injectable()
export class UrlService {

  constructor(private http:HttpClient, private router:Router) {}

  private urlMap:Map<string, string>=new Map();
  private urlsLoaded:boolean=false;
  private url$:BehaviorSubject<boolean>=new BehaviorSubject(this.urlsLoaded);


  //loads the file containing dynamic configuration
  public loadConfiguration():Observable<any> {
    let result=this.http.get<any>("./assets/config.json")
    return result;
  }

  //set the current environment to the one loaded
  public setEnvironment(env:any){
    environment.environment=env;
  }

  //query to the service base uri for api discovery
  public updateUrls()
  {
    this.http.get<any>(environment.environment.ApiRoot,  {observe: 'response'}).subscribe(resp=>{
      let links:string[] = resp.headers.get("Link").split(',');
      links.forEach(link=>{
        let segments:string[] = link.split(";");
				if (segments.length != 2){
          return;
        }
        let linkPart:string = segments[0].trim();
        linkPart = linkPart.substring(1, linkPart.length - 1);
        let relPart:string = segments[1].trim();
        relPart = relPart.substring(5, relPart.length - 1);
        this.urlMap.set(relPart,decodeURIComponent(linkPart).replace(/^(?:\/\/|[^\/]+)*\//,""));
      })
      if(this.urlsLoaded==false){
        this.urlsLoaded=true;
        this.url$.next(this.urlsLoaded);
      }
    })
  }

  //notifier in urls changes
  public getUrlsChanges():Observable<boolean>
  {
    return this.url$;
  }

  public isUrlsLoaded():boolean
  {
    return this.urlsLoaded;
  }

  public getUrl(url:string):string
  {
    return this.urlMap.get(url);
  }

  public goToHome()
  {
    this.router.navigate(['/home']);
  }
}
