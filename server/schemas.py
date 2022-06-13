from re import U
from pydantic import BaseModel


class Blacklist(BaseModel):
    user: str
    number: str

    class Config:
        orm_mode = True


class Whitelist(BaseModel):
    user: str
    number: str

    class Config:
        orm_mode = True


class Syncto(BaseModel):
    user: str
    syncto: str

    class Config:
        orm_mode = True


class UserBase(BaseModel):
    number: str


class UserCreate(UserBase):
    pass


class User(UserBase):
    blacklist: list[str] = []
    whitelist: list[str] = []
    sync: list[str] = []

    class Config:
        orm_mode = True


class PhonenumberBase(BaseModel):
    number: str


class PhonenumberCreate(PhonenumberBase):
    pass


class Number(PhonenumberBase):
    blacklist: list[str] = []
    # whitelist: list[str] = []

    class Config:
        orm_mode = True
