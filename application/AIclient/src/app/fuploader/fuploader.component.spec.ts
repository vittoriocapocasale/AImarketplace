import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FuploaderComponent } from './fuploader.component';

describe('FuploaderComponent', () => {
  let component: FuploaderComponent;
  let fixture: ComponentFixture<FuploaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FuploaderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FuploaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
