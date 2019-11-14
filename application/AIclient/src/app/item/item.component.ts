import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
//Not used
@Component({
  selector: 'app-item',
  templateUrl: './item.component.html',
  styleUrls: ['./item.component.css']
})
export class ItemComponent implements OnInit {

  constructor() { }

  @Input()
  phrase:string="";

  @Input()
  deleteButtonEnabled:boolean=false;


  @Input()
  downloadButtonEnabled:boolean=false;

  @Input()
  id:string=""

  @Output()
  delete$:EventEmitter<string>= new EventEmitter();

  @Output()
  download$:EventEmitter<string>= new EventEmitter();


  ngOnInit() {
  }

  downloadClicked()
  {
    this.download$.emit(this.id);
  }

  deleteClicked()
  {
    this.delete$.emit(this.id);
  }
}
