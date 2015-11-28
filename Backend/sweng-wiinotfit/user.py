import string
from google.appengine.ext import ndb

class User(ndb.Model):
    username = ndb.StringProperty()
    pswd = ndb.StringProperty()
    email = ndb.StringProperty()