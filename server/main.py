from typing import List

from fastapi import Depends, FastAPI, HTTPException
from sqlalchemy.orm import Session
import uvicorn

import crud
import models
import schemas
from database import SessionLocal, engine, resetDB

models.Base.metadata.create_all(bind=engine)

app = FastAPI()


# Dependency
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@app.on_event("startup")
async def startup():
    # resetDB()
    return


@app.put("/user/{user_number}/blacklist/{phone_number}")
def black_phonenumber(user_number: str, phone_number: str, db: Session = Depends(get_db)):
    db_user = crud.get_user(
        db=db, number=user_number)
    db_phonenumber = crud.get_phonenumber(
        db=db, number=phone_number)
    return crud.add_phonenumber_to_user_blacklist(db=db, user=db_user, phonenumber=db_phonenumber)


@app.put("/user/{user_number}/whitelist/{phonenumber}")
def white_phonenumber(user_number: str, phone_number: str, db: Session = Depends(get_db)):
    db_user = crud.get_user(
        db=db, number=user_number)
    db_phonenumber = crud.get_phonenumber(
        db=db, number=phone_number)
    return crud.add_phonenumber_to_user_whitelist(db=db, user=db_user, phonenumber=db_phonenumber)


@app.get("/blacklist/")
def get_blacklist(user_number: str, db: Session = Depends(get_db)):
    blacklist = crud.get_blacklist(db, user_number)
    return blacklist


@app.get("/whitelist/")
def get_whitelist(user_number: str, db: Session = Depends(get_db)):
    whitelist = crud.get_whitelist(db, user_number)
    return whitelist


@app.put("/sync/")
def sync_user_to_user(user1_number: str, user2_number: str, db: Session = Depends(get_db)):
    user1 = crud.get_user(db, number=user1_number)
    user2 = crud.get_user(db, number=user2_number)
    return crud.sync_user_to_user(db, user1, user2)


@app.delete("/sync/")
def unsync_user_to_user(user1_number: str, user2_number: str, db: Session = Depends(get_db)):
    user1 = crud.get_user(db, number=user1_number)
    user2 = crud.get_user(db, number=user2_number)
    return crud.unsync_user_to_user(db, user1, user2)


if __name__ == "__main__":
    uvicorn.run("main:app", reload=True)
