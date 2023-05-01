from django.db import models
# Create your models here.

class SearchDocument(models.Model):
    id = models.AutoField(primary_key=True)
    place_url = models.CharField(max_length=255, null=True)
    x = models.CharField(max_length=255, null=True)
    y = models.CharField(max_length=255, null=True)
    category_group_code=models.CharField(max_length=255,null=True)
    category_name=models.CharField(max_length=255,null=True)
    place_name=models.CharField(max_length=255,null=True)
    address=models.CharField(max_length=255,null=True)

    class Meta:
        managed = True
        db_table = "search_document"

class Category(models.Model):
    id = models.AutoField(primary_key=True)
    name=models.CharField(max_length=255,null=True)
    parent=models.ForeignKey('self',on_delete=models.CASCADE,null=True,related_name='recategory')

    class Meta:
        managed = True
        db_table = "category"

class Place(models.Model):
    id = models.AutoField(primary_key=True)
    place_url = models.CharField(max_length=255, null=True)
    x = models.CharField(max_length=255, null=True)
    y = models.CharField(max_length=255, null=True)
    place_name = models.CharField(max_length=255, null=True)
    address = models.CharField(max_length=255, null=True)
    star_rate=models.DecimalField(max_digits=3,decimal_places=2)
    category = models.ForeignKey(Category, on_delete=models.CASCADE, null=True)

    class Meta:
        managed = True
        db_table = "place"


