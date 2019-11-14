import { Injectable, EventEmitter, OnInit, SystemJsNgModuleLoader, OnDestroy } from '@angular/core';
import {User} from './user'
import { Observable, BehaviorSubject } from 'rxjs';
import * as moment from 'moment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UrlService } from './url.service';
import { Client } from './client';
var uriTemplates = require('uri-templates');

//consumes the api for user related queries
@Injectable()
export class IdentityService{

  private user:User;
  private user$:BehaviorSubject<User>;
  constructor(private http:HttpClient, private urlService:UrlService) {
    this.user=new User(null, null, null, null);
    this.user$=new BehaviorSubject<User>(this.user);
    if(this.isSessionNotExpired())
    {
      this.user.username = localStorage.getItem("name_user");
      this.user.role = localStorage.getItem("role_user");
    }

    this.user$.next(this.user);

   }

  isUserLogged():boolean
  {
    if(this.isSessionNotExpired()&&this.user.username)
    {
      return true;
    }
    return false;
  }

  isSessionNotExpired()
  {
    let expiration= this.getExpiration();
    if(expiration&&moment().isBefore(expiration))
    {
      return true;
    }
    return false;
  }
  getExpiration() {
    const expiration = localStorage.getItem("expires_at");
    const expiresAt = JSON.parse(expiration);
    return moment(expiresAt);
  }

  getToken() {
    return  localStorage.getItem("token");
  }

  getCurrentUsername():string
  {
    return this.user.username;
  }
  getCurrentUserRole():string
  {
    return this.user.role;
  }


  getUserChanges():Observable<User>
  {
    return this.user$;
  }

  login(username:string, password:string):Observable<any>
  {
    let headers= new HttpHeaders()
    headers = headers.append("Authorization", "Basic " + btoa("spring-security-oauth2-read-write-client:spring-security-oauth2-read-write-client-password1234"));
    headers = headers.append("Content-Type", "application/x-www-form-urlencoded");
    let httpOptions = {headers:headers};
    let body = new URLSearchParams();
    body.set('grant_type','password')
    body.set('username', username);
    body.set('password', password);
    body.set('client_id','spring-security-oauth2-read-write-client')
    return this.http.post(this.urlService.getUrl('token'),body.toString(),httpOptions);
  }

  setSession(authResult)
  {
    const expiresAt = moment().add(authResult.expires_in,'second');
    localStorage.setItem('token', authResult.access_token);
    localStorage.setItem('name_user', authResult.username);
    localStorage.setItem('role_user', authResult.role);
    localStorage.setItem("expires_at", JSON.stringify(expiresAt.valueOf()) );
    this.user.username=authResult.username;
    this.user.role=authResult.role;
    this.urlService.updateUrls();
    this.user$.next(this.user);
  }

  logout()
  {
    localStorage.removeItem("token");
    localStorage.removeItem("expires_at");
    localStorage.removeItem("name_user");
    localStorage.removeItem("role_user");
    if(this.user.username)
    {
      this.user.id=null;
      this.user.role=null;
    }
    this.urlService.updateUrls();
    this.user$.next(this.user);
  }

  registerUser(username:string, password:string):Observable<User>
  {
    let httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    let ret= this.http.post<User>(this.urlService.getUrl("users"),new User(null,username,password,null),httpOptions)
    return ret;
  }

  registerAdmin(username:string, password:string):Observable<User>
  {
    let httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    let ret= this.http.post<User>(this.urlService.getUrl("admins"),new User(null,username,password,null),httpOptions)
    return ret;
  }

  registerClient(username:string, password:string):Observable<Client>
  {
    let httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    let ret= this.http.post<Client>(this.urlService.getUrl("clients"),new Client(username,password),httpOptions)
    return ret;
  }

  getUserCredit():Observable<number>
  {
    let urlTemplate = uriTemplates(this.urlService.getUrl('credit'));
    let url=urlTemplate.fill({user: this.getCurrentUsername()});
    let ret= this.http.get<number>(url);
    return ret;
  }

  resetUserCredit():Observable<number>
  {
    let urlTemplate = uriTemplates(this.urlService.getUrl('credit'));
    let url=urlTemplate.fill({user: this.getCurrentUsername()});
    let ret= this.http.put<number>(url,null)
    return ret;
  }

  getUsernames():Observable<Array<string>>
  {
    let ret= this.http.get<Array<string>>(this.urlService.getUrl("users"))
    return ret;
  }


}
