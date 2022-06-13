from sqlalchemy import Column, ForeignKey, String, Table
from sqlalchemy.orm import relationship

from database import Base

Blacklist = Table(
    "blacklist",
    Base.metadata,
    Column("user", ForeignKey("users.number"),
           autoincrement=True, primary_key=True),
    Column("phonenumber", ForeignKey("phonenumbers.number"),
           autoincrement=True, primary_key=True)
)

Whitelist = Table(
    "whitelist",
    Base.metadata,
    Column("user", ForeignKey("users.number"),
           autoincrement=True, primary_key=True),
    Column("phonenumber", ForeignKey("phonenumbers.number"),
           autoincrement=True, primary_key=True)
)

Syncto = Table(
    "syncto",
    Base.metadata,
    Column("user", ForeignKey("users.number"),
           autoincrement=True, primary_key=True),
    Column("syncto", ForeignKey("users.number"),
           autoincrement=True, primary_key=True)
)


class User(Base):
    __tablename__ = "users"
    number = Column(String, primary_key=True,
                    autoincrement=True, unique=True, nullable=False)
    blacklist = relationship(
        "Phonenumber", secondary=Blacklist, back_populates="blacklist"
    )
    whitelist = relationship(
        "Phonenumber", secondary=Whitelist, back_populates="whitelist"
    )
    sync_with_users = relationship(
        "User", secondary=Syncto,
        primaryjoin=number == Syncto.c.user,
        secondaryjoin=number == Syncto.c.syncto,
        backref="sync_from_users"
    )


class Phonenumber(Base):
    __tablename__ = "phonenumbers"
    number = Column(String, primary_key=True,
                    autoincrement=True, unique=True, nullable=False)
    blacklist = relationship(
        "User", secondary=Blacklist, back_populates="blacklist"
    )
    whitelist = relationship(
        "User", secondary=Whitelist, back_populates="whitelist"
    )
