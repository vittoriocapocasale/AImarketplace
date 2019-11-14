import { Component, OnInit } from '@angular/core';
import { IdentityService } from '../identity.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})

//uses upform to register new user
export class RegistrationComponent implements OnInit {
  wrongCredentials:Boolean=false;
  constructor(private identityService:IdentityService,private router:Router) { }

  ngOnInit() {
  }

  register(credentials)
  {
    this.wrongCredentials=false;
    this.identityService.registerUser(credentials.username,credentials.password).subscribe(e=>{
      //console.log(e);
      this.router.navigate(["/login"]);
    },
    e=>{
      //console.log(e)
      this.wrongCredentials=true;
    })
  }

}
