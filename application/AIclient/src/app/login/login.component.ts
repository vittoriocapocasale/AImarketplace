import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { User } from '../user';
import { IdentityService } from '../identity.service';
import { runInThisContext } from 'vm';
import { Router } from '@angular/router';


//uses upform to perform login
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  wrongCredentials:boolean=false;
  constructor(private identityService:IdentityService, private router:Router) {}

  ngOnInit() {}


  login(credentials)
  {
    this.wrongCredentials=false;
    this.identityService.login(credentials.username, credentials.password).subscribe(resp=>{
      this.identityService.setSession(resp);
      let role=this.identityService.getCurrentUserRole();
      let userId=this.identityService.getCurrentUsername();
      this.router.navigate([`/login/${role}/${userId}`]);
    },
    resp=>{this.wrongCredentials=true;})
  }

}
