from app import db
from sqlalchemy import Table, Column, Integer, ForeignKey, DateTime
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base
from datetime import datetime

Base = declarative_base()

room_challenge = Table('association', Base.metadata,
    Column('Room', Integer, ForeignKey('Room.id')),
    Column('Challenge', Integer, ForeignKey('Challenge.id'))
)
class Room(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, unique=True)
    gpsposition = db.Column(db.String(200))
    points = db.Column(db.Integer)
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)
    challenges = relationship("Challenges", secondary=room_challenge, back_populates="rooms")

class Challenge(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, unique=True)
    rooms = relationship("Rooms", secondary=room_challenge, back_populates="challenges")

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, unique=True)
    points = db.Column(db.Integer)
