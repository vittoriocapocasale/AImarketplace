import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpformComponent } from './upform.component';

describe('UpformComponent', () => {
  let component: UpformComponent;
  let fixture: ComponentFixture<UpformComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UpformComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
