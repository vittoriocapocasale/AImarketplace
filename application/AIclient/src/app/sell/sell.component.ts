import { Component, OnInit } from '@angular/core';
import { PositionService } from '../position.service';
import { FormGroup, FormControl } from '@angular/forms';
import { Archive } from '../archive';
import {Position}  from '../position'
import { from } from 'rxjs';
@Component({
  selector: 'app-sell',
  templateUrl: './sell.component.html',
  styleUrls: ['./sell.component.css']
})

//form to publish an archive

export class SellComponent implements OnInit {

  constructor(private positionService:PositionService) { }
  positions:Position[]=null;
  price:number=0;
  sellN:boolean=false;
  sellY:boolean=false;
  form: FormGroup = new FormGroup({
    price: new FormControl(0),
    tagName: new FormControl(''),
  });

  ngOnInit() {
  }



  //called sub comonent emits the file contents, this stores them
  fileReady(positions:Position[])
  {
    console.log(positions);
    this.positions=positions;
  }

  //publish current data to the server
  sell()
  {
    this.sellN=false;
    this.sellY=false;
    if(!this.positions||(!this.form.valid)||this.form.value.tagName=='')
    {
      //console.log(this.positions, this.form.valid, "T:",this.form.value.tagName);
      this.sellN=true;
      return;
    }
    let a:Archive =new Archive(this.form.value.tagName, null, this.form.value.price, null, this.positions);
    console.log(a);
    this.positionService.publishArchive(a).subscribe(e=>{this.sellY=true}, e=>{this.sellN=true});
  }

}
