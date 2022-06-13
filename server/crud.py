from sqlalchemy.orm import Session

import models
import schemas


def get_user(db: Session, number: str):
    user = db.query(models.User).filter(models.User.number == number).first()
    if user is None:
        user = create_user(db, schemas.UserCreate(number=number))
    return user


# def get_users(db: Session, skip: int = 0, limit: int = 100):
#     return db.query(models.User).offset(skip).limit(limit).all()


def create_user(db: Session, user: schemas.UserCreate):
    db_user = models.User(number=user.number)
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user


def get_phonenumber(db: Session, number: str):
    phonenumber = db.query(models.Phonenumber).filter(
        models.Phonenumber.number == number).first()
    if phonenumber is None:
        phonenumber = create_phonenumber(
            db, schemas.PhonenumberCreate(number=number))
    return phonenumber


# def get_phonenumbers(db: Session, skip: int = 0, limit: int = 100):
#     return db.query(models.Phonenumber).offset(skip).limit(limit).all()


def create_phonenumber(db: Session, phonenumber: schemas.PhonenumberCreate):
    db_phonenumber = models.Phonenumber(number=phonenumber.number)
    db.add(db_phonenumber)
    db.commit()
    db.refresh(db_phonenumber)
    return db_phonenumber


def get_blacklist(db: Session, user_number: str):
    user = db.query(models.User).filter(
        models.User.number == user_number).first()
    return user.blacklist


def get_whitelist(db: Session, user_number: str):
    user = db.query(models.User).filter(
        models.User.number == user_number).first()
    return user.whitelist


def add_phonenumber_to_user_blacklist(db: Session, user: models.User, phonenumber: models.Phonenumber):
    if phonenumber in user.whitelist:
        user.whitelist.remove(phonenumber)
    user.blacklist.append(phonenumber)
    db.commit()
    return phonenumber


def add_phonenumber_to_user_whitelist(db: Session, user: models.User, phonenumber: models.Phonenumber):
    if phonenumber in user.blacklist:
        user.blacklist.remove(phonenumber)
    user.whitelist.append(phonenumber)
    db.commit()
    return phonenumber


def sync_user_to_user(db: Session, user1: models.User, user2: models.User):
    user1.sync_with_users.append(user2)
    db.commit()
    return user1


def unsync_user_to_user(db: Session, user1: models.User, user2: models.User):
    if user2 in user1.sync_with_users:
        user1.sync_with_users.remove(user2)
    db.commit()
    return user1
