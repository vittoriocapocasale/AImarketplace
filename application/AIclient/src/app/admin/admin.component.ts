import { Component, OnInit } from '@angular/core';
import { IdentityService } from '../identity.service';
import { ThrowStmt } from '@angular/compiler';

//uses to upform to register a client or another admin

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  wrongClientCredentials:Boolean=false;
  wrongAdminCredentials:Boolean=false;
  goodClientCredentials:Boolean=false;
  goodAdminCredentials:Boolean=false;
  constructor(private identityService:IdentityService) { }

  ngOnInit() {
  }

  registerClient(credentials)
  {
    this.goodClientCredentials=false;
    this.wrongClientCredentials=false;
    this.identityService.registerClient(credentials.username,credentials.password).subscribe(e=>{
      this.goodClientCredentials=true;
    },
    e=>{
      console.log(e);
      this.wrongClientCredentials=true;
    })
  }

  registerAdmin(credentials)
  {
    this.goodAdminCredentials=false;
    this.wrongAdminCredentials=false;
    this.identityService.registerAdmin(credentials.username,credentials.password).subscribe(e=>{
      this.goodAdminCredentials=true;
    },
    e=>{
      console.log(e);
      this.wrongAdminCredentials=true;
    })
  }

}
