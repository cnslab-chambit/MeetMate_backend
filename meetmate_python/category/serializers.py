from rest_framework import serializers
from .models import Category,Place,SearchDocument

class CategorySerializer(serializers.ModelSerializer) :
    parent=serializers.PrimaryKeyRelatedField(
        queryset=Category.objects.all(),
        allow_null=True,
        required=False
    )
    class Meta :
        model = Category
        fields = '__all__'

class PlaceSerializer(serializers.ModelSerializer) :
    category = serializers.PrimaryKeyRelatedField(
        queryset=Category.objects.all(),
        allow_null=False,
        required=True
    )

    class Meta :
        model = Place
        fields = '__all__'

class SearchDocumentSerializer(serializers.ModelSerializer) :
    class Meta :
        model = SearchDocument
        fields = '__all__'


