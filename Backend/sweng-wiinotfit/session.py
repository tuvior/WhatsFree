import string
from google.appengine.ext import ndb

class Session(ndb.Model):
    cookie = ndb.StringProperty()
    user = ndb.StringProperty()