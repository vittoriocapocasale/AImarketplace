import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { Observable } from 'rxjs';
//load a file from local system
@Component({
  selector: 'app-fuploader',
  templateUrl: './fuploader.component.html',
  styleUrls: ['./fuploader.component.css']
})
export class FuploaderComponent implements OnInit {

  constructor() { }

  //emits the positions found in the file loaded
  @Output()
  file$:EventEmitter<Position[]>=new EventEmitter<Position[]>();

  ngOnInit() {
  }

  //parses file and emits positions
  fileChanged(f:any)
  {
    console.log(f);
    if(!f.target.files[0])
    {
      return;
    }
    let fileReader = new FileReader();
    fileReader.readAsText(f.target.files[0], 'utf8');
    fileReader.onload = () => {
      let positions = JSON.parse(fileReader.result.toString());
      console.log(positions);
      this.file$.emit(positions);
    }
  }


}
