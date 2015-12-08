import string
from google.appengine.ext import ndb

class Submission(ndb.Model):
    name = ndb.StringProperty()
    lowerName = ndb.StringProperty()
    category = ndb.StringProperty()
    description = ndb.StringProperty()
    location = ndb.StringProperty()
    image = ndb.BlobProperty()
    keywords = ndb.StringProperty()
    rating = ndb.IntegerProperty()
    submitter = ndb.StringProperty()
    submitted = ndb.DateTimeProperty(auto_now_add=True)
    tfrom = ndb.DateTimeProperty()
    tto = ndb.DateTimeProperty()
    latitude = ndb.FloatProperty()
    longitude = ndb.FloatProperty()